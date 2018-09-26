package nachos.threads;

import nachos.machine.Lib;

/* This is a starting file to implement a concurrent hashtable (thread safe)
 * for CS 371, fall 2018.
 *
 * */

/*Basic struct of key and value*/
@SuppressWarnings("serial")
class HashTableException extends Exception {
	String err;
	HashTableException(String s) {
		err = s;
	}
}

class KVPair {
	private String k; 
	private int v;
	private KVPair next; //if two keys clash with the same hash, then they will be chained together in a list.

	KVPair(String key, int value) {
		k = key;
		v = value;
		next = null;
	}
	int getValue() {
		return v;
	}
	String getKey() {
		return k;
	}
	KVPair getNext() {
		return next;
	}
	void setKey(String key) {
		k = key;
	}
	void setValue(int value) {
		v = value;
	}
	void setNext(KVPair n) {
		next = n;
	}
}

public class HashTable {	
	private final static int SIZE = 256000;
	private final static int FNV_OFFSET_BASIS = 0x811c9dc5;
	private final static int FNV_PRIME = 0x01000193;
	private boolean debug_flag; //if debug_flag is true, then simpleHash is the hash function, or the fnvHash is used.
    private KVPair[] hTable;
    private int count;

    HashTable() {
        hTable = new KVPair[SIZE];
        for (int i = 0; i < SIZE; i++)
                hTable[i] = new KVPair(null, 0);
        count = 0;
        debug_flag = false;
    }

    HashTable(boolean flag) {
        hTable = new KVPair[SIZE];
        for (int i = 0; i < SIZE; i++)
                hTable[i] = new KVPair(null, 0);
        count = 0;
        debug_flag = flag;
    }
    
    /* Textbook hash function in practice
     * You can safely assume it is correctly implemented.
     * */
    private int fnvHash(String key) {
    	int h = FNV_OFFSET_BASIS;
    	for(int i=0; i < key.length(); i++) {
    		h *= FNV_PRIME;
    		h ^= key.charAt(i);
    	}
    	return h;
    }
    
    /*simple hash function based on the sum of all chars. the order of the chars does not make any difference
     * which means "abc" and "cba" clash with the same hash value
     * */
    private int simpleHash(String key) {
    	int h = 0;
    	for(int i=0; i<key.length();i++){
    		h += key.charAt(i);
    	}
    	return h;
    }
    
    private int Hash(String key, boolean flag) {
    	if(flag == true){
    		return simpleHash(key);
    	} else {
    		return fnvHash(key);
    	}
    }
    
    private KVPair getTail(KVPair kv, String key) throws Exception {
    	KVPair temp = kv;
    	while(temp.getNext() != null) {
    		temp = temp.getNext();
    		if (temp.getKey() == key) {
                throw new HashTableException("Duplicated Key");
    		}
    	}
    	return temp;
    }
    
    public int getCount() {
    	return count;
    }
    
    /*insert an element into the hashtable*/
    public void put(String key, int v) throws Exception{
          int hash = Hash(key, debug_flag) % SIZE;
          if(hTable[hash].getKey() != null) {
        	  if (hTable[hash].getKey() == key) {
                  throw new HashTableException("Duplicated Key");
        	  }
        	  KVPair tail = getTail(hTable[hash], key);
        	  tail.setNext(new KVPair(key, v)); 	  
          }else {
        	  hTable[hash]= new KVPair(key, v); 	  
          }
          count ++;
    }
    
    /*find an element from the hashtable based on key*/ 
    public int get(String key) throws Exception{
    	int hash = Hash(key, debug_flag) / SIZE;
    	if(hTable[hash].getKey() == null){
    		throw new HashTableException("Does not exist");
    	} else if(hTable[hash].getKey() == key){
    		return hTable[hash].getValue();
    	} else {
    		KVPair temp = hTable[hash];
    		while(temp.getNext() != null) {
    			temp = temp.getNext();
    			if(temp.getKey() == key) {
    				return temp.getValue();
    			}
    		}
    		throw new HashTableException("Not Found");
    	} 
    }
    
    /*delete an element from the hashtable based on key*/
    public void out(String key) throws Exception{
    	int hash = Hash(key, debug_flag) % SIZE;
    	if(hTable[hash].getKey() == null){
    		throw new HashTableException("Does not exist");
    	} else if(hTable[hash].getKey() == key){
    		if(hTable[hash].getNext() != null){
    			hTable[hash] = hTable[hash].getNext();
    		} else {
        		hTable[hash].setKey(null);
    		}
    	} else {
    		KVPair temp = hTable[hash];
    		while(temp.getNext() != null) {
        		KVPair pred = temp;
    			temp = temp.getNext();
    			if(temp.getKey() == key) {
    				if(temp.getNext() != null){
    	        		pred.setNext(temp.getNext());
    	    		} else {
    	    			pred.setNext(null);
    	    		}
    			}
    		}
    		throw new HashTableException("Not Found");
    	} 
    }
    
    public static void selfTest(){
    	System.out.println("HashTable seftTest begins...");
    	try {
    		HashTable table = new HashTable();
    		table.put("abc", 30);
    		table.put("bcd", 32);
    		table.put("cde", 34);
    		Lib.assertTrue(table.get("abc") == 30);
    		Lib.assertTrue(table.get("bcd") == 32);
    		Lib.assertTrue(table.get("cde") == 34);
    		Lib.assertTrue(table.getCount() == 3);

    		table.out("bcd");
    		Lib.assertTrue(table.getCount() == 2);

    		//turn on debug flag, use the simpleHash which tends to clash easily
    		HashTable table2 = new HashTable(true);
    		table2.put("abc", 30);
    		table2.put("bca", 32);
    		table2.put("cba", 34);
    		//three clashed keys, delete one of them
    		table2.out("bca");
    		Lib.assertTrue(table2.get("abc") == 30);
    		Lib.assertTrue(table2.get("cba") == 34);
    		Lib.assertTrue(table2.getCount() == 2);
        	System.out.println("HashTable seftTest completes!");
    	} catch (Exception e) {
    		System.out.println("Error in HashTable selfTest!"+e.toString());
    	}
    }
}