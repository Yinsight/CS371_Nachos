package nachos.threads;

import java.util.concurrent.Semaphore;

public class LinkedList {
	// Class variables for the Linked List
	private Node head;
	private int numNodes;
	private Semaphore sem;


	public LinkedList(int numNodes) {
		//head = new Node(i);
		this.numNodes = numNodes;
		sem = new Semaphore(1);
	}


	public void add(int key, int value) throws LinkedListException {
	
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			throw new LinkedListException("Sem fail to acquire");
		}
		//head.acquire();
		
		Node temp = new Node(key, value);
		
		temp.next = head;
		
		head = temp;
		
		numNodes++;
		//head.release();
		sem.release();
	}

	public void delete(int key) throws LinkedListException{
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			throw new LinkedListException("Semaphore failed to acquire");
		}
		//head.acquire();
		Node pre = head;
		Node cur = head;
		
		//special case
		if(cur.key == key){
			head = head.next;
			sem.release();
			return;
		}
		
		while(cur.key!=key && cur.next != null){
			pre = cur;
			cur = cur.next;
		}
		
		if (cur.next == null){
			throw new LinkedListException("Cannot find the key to delete");
		}
		
		if (cur.key == key){
			pre.next = cur.next;
			sem.release();
			return;
		}
		
		numNodes--;
		//head.release();
		//sem.release();
		
	}


	public int find(int key) throws LinkedListException {

		try {
			sem.acquire();
		} catch (InterruptedException e) {
			throw new LinkedListException("Semaphore failed to acquire");
		}
		//head.acquire();
		
		Node pre = head;
		Node cur = head;
		
		while(cur.key!=key && cur.next != null){
			pre = cur;
			cur = cur.next;
		}
		
		if (cur.next == null){
			throw new LinkedListException("Cannot find the key");
		}else {
			sem.release();
			return cur.value;
		}
		
		//sem.release();
		//head.release();
	
	}

	public int getSize() {
		return numNodes;
	}

	public class Node {

		// Declare class variables
		private Node next;
		private int value;
		private int key;
		Semaphore sem;

		public Node(int key, int value) {
			sem = new Semaphore(1);
			this.value = value;
			this.key = key;
		}

	}

		
	}

