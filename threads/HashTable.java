package nachos.threads;

import nachos.machine.Lib;
import nachos.threads.LinkedList;
import nachos.threads.BridgeCrossing.Bridge;
import nachos.threads.LinkedList.Node;
import nachos.threads.LinkedListException;

public class HashTable {
	
	private int lengthofTable;
	private LinkedList[] LL;
	//private int index;
	//private int size;
	
	
	public HashTable(int n_buckets) {
		lengthofTable = n_buckets;
		LL = new LinkedList[n_buckets];
		for (int i=0;i < n_buckets;i++){
			LL[i] = new LinkedList(0);
		}
	}

	public int hash(int k) {
		int i = k % lengthofTable;
		return i;
	}
	
	public int size(){
		return lengthofTable;
	}

	public void insert(int k, int val) throws LinkedListException {
		
		int temp = hash(k);
		try {
			int v = LL[temp].find(k);
		}catch(LinkedListException e) {
			LL[temp].add(k,val);
			return;	
		}
		throw new LinkedListException("Duplicate Keys");
		}
		

	public void remove(int k) throws LinkedListException {
		
		int temp = hash(k);
		try{
			int v = LL[temp].find(k);
		} catch (LinkedListException e){
			throw new LinkedListException("Cannot find key");
		}
		LL[temp].delete(k);
		return;
		
		}
		

	public int get(int k) throws LinkedListException {
		
		int temp = hash(k);
		int v;
		try{
			v = LL[temp].find(k);
		} catch (LinkedListException e){
			throw new LinkedListException("Cannot find key");
		}
		return v;
		
	}

	public int getbucketsize() {
		
		return lengthofTable;
		
	}
	
 enum OperationType{
		INSERT,
		REMOVE,
		QUERY
	}
	
 static class ThreadOperation{
 		int k;
 		OperationType op;
 		int result;
		
		public ThreadOperation(int k, OperationType op, int result){
			this.k = k;
			this.op = op;
			this.result = result;
		}

	}

	public void batch(int n_ops, ThreadOperation[] ops) throws LinkedListException{
		
		KThread array[] = new KThread[n_ops];
		
		for (int i=0;i<n_ops;i++){
			final ThreadOperation op = ops [i];
			KThread operation = new KThread(new Runnable(){
				public void run(){
					if(op.op == OperationType.INSERT){
						try {
							insert(op.k,op.result);
						} catch (LinkedListException e) {
							e.printStackTrace();
						}
				
					}
					else if (op.op == OperationType.REMOVE){
						try {
							remove(op.k);
						} catch (LinkedListException e) {
							e.printStackTrace();
						}
					}
					else if (op.op == OperationType.QUERY){
						try {
							op.result = get(op.k);
						} catch (LinkedListException e) {
							e.printStackTrace();
						}
					}
					else {
						Lib.assertNotReached();
					}
				}
			}
			);
			operation.fork();
			array[i] = operation;
		}
		
		array[n_ops].join();
		
		/*
		for (int j=0;j<n_ops;j++){
			KThread array[] = new KThread[n_ops];
			array[j].join();
		}
		*/
		
		
	}		
	
	
	public int getNumElement(){
		int sum = 0;
		for (int i=0;i<lengthofTable;i++){
			sum += LL[i].numNodes;
		}
		
		//System.out.println("Length of Table is:" + sum);
		return sum;
	}
	

}
	
	