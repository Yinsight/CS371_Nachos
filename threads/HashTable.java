package nachos.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import nachos.threads.LinkedList;
import nachos.threads.LinkedList.Node;

public class HashTable {
	
	enum OperationType {
		INSERT, REMOVE, QUERY
	}

	class ThreadOperation {
		int k;
		OperationType op;
		int result;
	};

	ThreadOperation getThreadOperation() {
		return new ThreadOperation();
	}

	class Key {
		LinkedList list = new LinkedList(Integer.MIN_VALUE);
		public Key() {
		}
	}

	private int count;
	Semaphore countSem;
	private final int lengthOfTable;
	private Key[] keys;
	private ExecutorService pool;

	public HashTable(int n) {
		count = 0;
		countSem = new Semaphore(1);
		lengthOfTable = n;
		keys = new Key[n];
		for (int i = 0; i < n; i++) {
			keys[i] = new Key();
		}
		pool = Executors.newCachedThreadPool();
	}

	public int hash(int k) {
		return k % lengthOfTable;
	}

	public void insert(int k, int val) {
		System.out.println("insert");
		k = hash(k);
		if (k < 0 || k >= lengthOfTable) {
			System.err.println("wrong K");
			return;
		}
		try {
			keys[k].list.add(val);
			countSem.acquire();
			count++;
			countSem.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void remove(int k) {
		System.out.println("remove");
		int key = k;
		k = hash(k);
		if (k < 0 || k >= lengthOfTable) {
			System.err.println("wrong K");
			throw new IllegalArgumentException("wrong K");
		}
		LinkedList list = keys[k].list;
		if (list.findByValue(key) > 0) {
			list.remove(key);
			try {
				countSem.acquire();
				count--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				countSem.release();
			}
		}
	}

	public int get(int k) throws Exception {
		System.out.println("get");
		int key = k;
		k = hash(k);
		if (k < 0 || k >= lengthOfTable) {
			System.err.println("wrong K");
			throw new IllegalArgumentException("wrong K");
		}
		if (keys[k].list.findByValue(key) != -1) {
			return key;
		}
		throw new IllegalArgumentException("wrong K");
	}

	public int getbucketsize() {
		return count;
	}

	public void batch(int n, ThreadOperation[] ops) {
		Future[] futures = new Future[n];
		int i = 0;
		for (final ThreadOperation threadOperation : ops) {
			switch (threadOperation.op) {
			case INSERT:
				futures[i++] = pool.submit(new Runnable() {
					@Override
					public void run() {
						insert(threadOperation.k, threadOperation.result);
					}
				});
				break;
			case QUERY:
				futures[i++] = pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							threadOperation.result = get(threadOperation.k);
							System.out.println(threadOperation.result);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}
				});
				break;
			case REMOVE:
				futures[i++] = pool.submit(new Runnable() {
					@Override
					public void run() {
						try {
							remove(threadOperation.k);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}
				});
				break;
			default:
				break;
			}
		}
		try {
			//wait for every thread to complete
			for (Future item : futures) {
				if (item != null) {
					item.get();
					System.out.println("---future.idDone()" + item.isDone());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		HashTable table = new HashTable(10);
		ThreadOperation[] ops = new ThreadOperation[5];
		for (int i = 0; i < 5; i++) {
			ops[i] = table.getThreadOperation();
		}
		ops[0].k = 11;
		ops[0].op = OperationType.INSERT;
		ops[0].result = 9;
		ops[1].k = 13;
		ops[1].op = OperationType.INSERT;
		ops[1].result = 3;
		ops[2].k = 14;
		ops[2].op = OperationType.INSERT;
		ops[2].result = 4;
		ops[3].k = 24;
		ops[3].op = OperationType.INSERT;
		ops[3].result = 6;
		ops[4].k = 24;
		ops[4].op = OperationType.REMOVE;
		ops[4].result = 1;
		table.batch(5, ops);
		ThreadOperation[] ops2 = new ThreadOperation[1];
		ops2[0] = table.getThreadOperation();
		ops2[0].k = 14;
		ops2[0].op = OperationType.QUERY;
		ops2[0].result = 9;
		table.batch(1, ops2);
		table.shutdown();
	}

	private void shutdown() {
		//shut down here
		pool.shutdownNow();
	}
}
