package nachos.threads;

import java.util.concurrent.Semaphore;

public class LinkedList {
	// Class variables for the Linked List
	private Node head;
	private int numNodes;
	private Semaphore sem;

	public static void main(String[] args) throws InterruptedException {

	}

	public LinkedList(int i) {

		head = new Node(i);
		numNodes = 0;
		sem = new Semaphore(1);

	}

	public Node intanceOfNode(int i) {

		return new Node(i);

	}

	public void add(int i) throws InterruptedException {

		Node pre = head;
		Node now = head.next;
		while (now != null) {
			pre = now;
			now = now.next;
		}

		pre.sem.acquire();
		pre.next = new Node(i);
		pre.sem.release();
		sem.acquire();
		numNodes++;
		sem.release();
		
	}

	public void deleteAtIndex(int index) throws InterruptedException {

		Node temp = head;
		
		for (int i = 0; i < index - 1 && temp.next != null; i++) {
			temp = temp.next;
		}

		temp.sem.acquire();
		temp.next = temp.next.next;
		temp.sem.release();
		sem.acquire();
		numNodes--;
		sem.release();
		
	}

	public int find(Node n) {

		Node t = head;
		int index = 0;
		while (t != null && t.data != n.data) {
			index++;
			t = t.next;
		}
		return index;
	}

	public int findByValue(int value) {

		int index = find(new Node(value));
		if (index >= getSize()) {
			return -1;
		}
		return index;
	}

	public Node find(int index) {

		head.acquire();
		Node temp = head;
		
		for (int i = 0; i < index; i++) {

			temp.next.acquire();
			Node tempFortemp = temp;
			temp = temp.next;
			tempFortemp.release();
			
		}

		temp.release();
		return temp;

	}

	public void printList() {

		Node temp = head;
		
		while (temp != null) {
			System.out.println(temp.data);
			temp = temp.next;
		}
	}

	public int countList() {

		int count = 0;
		Node temp = head;
		
		while (temp != null) {
			count++;
			temp = temp.next;
		}
		return count - 1;
	}

	public int getSize() {

		return numNodes;

	}

	public class Node {

		// Declare class variables
		private Node next;
		private int data;
		Semaphore sem;

		public Node(int i) {

			sem = new Semaphore(1);
			data = i;

		}

		public int getData() {
			return data;
		}

		public void acquire() {

			try {
				sem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				sem.release();
			}
		}

		public void release() {

			sem.release();

		}
	}

	public void remove(int key) {

		int index = find(new Node(key));
		if (index >= numNodes) {
			return;
		}
		try {
			deleteAtIndex(index);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
