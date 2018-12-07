package nachos.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import nachos.threads.LinkedList;
import nachos.threads.LinkedList.Node;

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
		
		
		
	}

	public int get(int k) {
		
	}

	public int getbucketsize() {
		
	}

	public void batch(int n_ops, ThreadOperation[] ops) {
		
			
	}		
	
	enum OperationType{
		INSERT,
		REMOVE,
		QUERY
	}
	
	Class ThreadOperation{
		int k;
		OperationType op;
		int result;
	}
			

	public static void main(String[] args) {
		
		
	}


}
