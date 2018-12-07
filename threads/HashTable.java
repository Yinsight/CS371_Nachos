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
	}

	public int hash(int k) {
		int i = k % lengthofTable;
		return i;
	}
	
	public int size(){
		return lengthofTable;
	}

	public void insert(int k, int val) {
		
		int temp = hash(k);
		try {
			int v = LL[temp].find(k);
		}catch(LinkedListException e) {
			LL[temp].add(k,val);
			return;	
		}
		throw new LinkedListException("Duplicate Keys");
		}
		

	public void remove(int k) {
		
		int temp = hash(k);
		try{
			int v = LL[temp].find(k);
		} catch (LinkedListException e){
			throw new LinkedListException("Cannot find key");
		}
		LL[temp].delete(k);
		return;
		
		}
		

	public int get(int k) {
		
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
	
	protected class ThreadOperation{
		int k;
		OperationType op;
		int result;
	}

	public void batch(int n_ops, ThreadOperation[] ops) {
		
		for (int i=0;i<n_ops;i++){
			final ThreadOperation op = ops [i];
			KThread operation = new KThread(new Runnable(){
				public void run(){
					if(op.op == OperationType.INSERT){
						insert(op.k,op.result);
					}
					else if (op.op == OperationType.REMOVE){
						remove(op.k);
					}
					else if (op.op == OperationType.QUERY){
						op.result = get(op.k);
					}
					else {
						Lib.assertNotReached();
					}
				}
			}
			);
		}
			
	}		
	
	public static void selfTest() {
		
		KThread array[] = new KThread[10];
			
		for(int i=0;i<10;i++){
			
			KThread operation = new KThread(new Runnable(){
				
						
			public void run() {
				
				HashTable.insert(2,4);
				HashTable.remove(1);
				HashTable.get(1);	
			
			}
		}
		);
			operation.fork();
			array[i]=operation;
		}
		
		
		KThread array1[] = new KThread[10];
		
		for(int i=0;i<10;i++){
			
			KThread operation = new KThread(new Runnable(){
			
			public void run() {
				
				HashTable.insert(3,5);
				HashTable.remove(5);
				HashTable.get(3);	
			
			}
			
		}
		);
			operation.fork();
			array1[i]=operation;
		}
		
		
		for(int j=0;j<10;j++){
			array[j].join();
			array1[j].join();
		}
			
		    }
	
	public static void main(String[] args) {
		
	}
}
	
	