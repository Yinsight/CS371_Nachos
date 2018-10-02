package nachos.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HashTable {
	
	enum OperationType {
		INSERT,
		REMOVE,
		QUERY
	}

	class ThreadOperation
	{
		int k;
		OperationType op;
		int result;
	};

	ThreadOperation getThreadOperation() {
		return new ThreadOperation();
	}

	class Value {
		int key;
		int value;
		Value next;
	}

	class Key {
		Value values;
		public Key() {
			values = null;
		}
	}

	private int count;
	private Key[] keys;
	private final int lengthOfTable;
	private ExecutorService pool;
	public HashTable(int n) {
		count = 0;
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
		int key = k;
		k = hash(k);
		
		if (k < 0 || k >= lengthOfTable) {
			System.err.println("wrong K");
			return;
		}
		try {
			Value tmpInsert = new Value();
			tmpInsert.key = key;
			tmpInsert.value = val;
			tmpInsert.next = keys[k].values;
			keys[k].values = tmpInsert;
		} finally {
			count++;
		}
	}

	public void remove(int k) throws Exception {
		System.out.println("remove");
		int key = k;
		k = hash(k);
		
		if (k < 0 || k >= lengthOfTable) {
			System.err.println("wrong K");
			throw new IllegalArgumentException("wrong K");
		}
		try {
			Value head = keys[k].values;
			if (head == null) {
				throw new Exception("do not include the key");
			}
			if (head.key == key) {
				head = head.next;
				return;
			}

			Value before = head;
			Value now = head.next;
			
			while (now != null) {
				if (now.key == key) {
					before.next = now.next;
				}
				
				before = before.next;
				now = now.next;
			}
			throw new Exception("do not include the key");
			
		} finally {

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
		try {
			Value head = keys[k].values;
			while (head != null) {
				if (head.key == key) {
					return head.value;
				}
				head = head.next;
			}
		} finally {

		}
		throw new Exception("do not include the key");

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
			for (Future item : futures) {
				if (item != null) {
					item.get();
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
		pool.shutdownNow();
	}

}
