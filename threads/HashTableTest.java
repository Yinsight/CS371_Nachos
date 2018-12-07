package nachos.threads;

import nachos.machine.Lib;
import nachos.threads.HashTable.OperationType;
import nachos.threads.HashTable.ThreadOperation;

public class HashTableTest {
	public static void selfTest() throws LinkedListException {
		
		int n_ops = 100;
	
		ThreadOperation[] ops = new ThreadOperation[n_ops];
		
		for(int i=0;i<n_ops;i++){
			ops[i] = new ThreadOperation(i, OperationType.INSERT, i);
		
		}
		HashTable table = new HashTable(1000);
		
			table.batch(n_ops, ops);
			table.getNumElement();
			
			if (table.getNumElement() == n_ops){
				System.out.println("Test passed!");
			}else{
				Lib.assertNotReached();
			}
			
	}	
	
}
