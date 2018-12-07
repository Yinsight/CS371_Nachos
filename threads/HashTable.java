package nachos.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import nachos.threads.LinkedList;
import nachos.threads.LinkedList.Node;

public class HashTable {
	
	public HashTable(int n_buckets) {
	

	}

	public int hash(int k) {
		return k % lengthOfTable;
	}

	public void insert(int k, int val) {
		
		
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
