package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;
import java.util.HashMap.*;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Stack;

/**
 * A kernel that can support multiple user processes.
 */
public class UserKernel extends ThreadedKernel {
	/**
	 * Allocate a new user kernel.
	 */
	public UserKernel() {
		super();
		for (int i = 0; i < Machine.processor().getNumPhysPages(); i++) {
			freePages.put(i, 0);
		}
	}

	/**
	 * Initialize this kernel. Creates a synchronized console and sets the
	 * processor's exception handler.
	 */
	public void initialize(String[] args) {
		super.initialize(args);

		console = new SynchConsole(Machine.console());

		Machine.processor().setExceptionHandler(new Runnable() {
			public void run() {
				exceptionHandler();
			}
		});
	}

	/**
	 * Test the console device.
	 */
	public void selfTest() {
		super.selfTest();

		System.out.println("Testing the console device. Typed characters");
		System.out.println("will be echoed until q is typed.");

		char c;

		do {
			c = (char) console.readByte(true);
			console.writeByte(c);
		} while (c != 'q');

		System.out.println("");
	}

	/**
	 * Returns the current process.
	 * 
	 * @return the current process, or <tt>null</tt> if no process is current.
	 */
	public static UserProcess currentProcess() {
		if (!(KThread.currentThread() instanceof UThread))
			return null;

		return ((UThread) KThread.currentThread()).process;
	}

	public static int getFreePage() {
		return getFreePages(1)[0];
	}

	public static int[] getFreePages(int numOfPages) {
		int[] availablePage = new int[numOfPages];
		while (numOfPages > 0) {
			for (int i = 0; i < Machine.processor().getNumPhysPages(); i++) {
				lock.acquire();
				availablePage[i] = freePages.get(i);
				numOfPages--;
				lock.release();
			}
		}
		return availablePage;
	}

	/**
	 * The exception handler. This handler is called by the processor whenever a
	 * user instruction causes a processor exception.
	 * 
	 * <p>
	 * When the exception handler is invoked, interrupts are enabled, and the
	 * processor's cause register contains an integer identifying the cause of
	 * the exception (see the <tt>exceptionZZZ</tt> constants in the
	 * <tt>Processor</tt> class). If the exception involves a bad virtual
	 * address (e.g. page fault, TLB miss, read-only, bus error, or address
	 * error), the processor's BadVAddr register identifies the virtual address
	 * that caused the exception.
	 */
	public void exceptionHandler() {
		Lib.assertTrue(KThread.currentThread() instanceof UThread);

		UserProcess process = ((UThread) KThread.currentThread()).process;
		int cause = Machine.processor().readRegister(Processor.regCause);
		process.handleException(cause);
	}

	/**
	 * Start running user programs, by creating a process and running a shell
	 * program in it. The name of the shell program it must run is returned by
	 * <tt>Machine.getShellProgramName()</tt>.
	 * 
	 * @see nachos.machine.Machine#getShellProgramName
	 */
	public void run() {
		super.run();

		UserProcess process = UserProcess.newUserProcess();

		String shellProgram = Machine.getShellProgramName();
		Lib.assertTrue(process.execute(shellProgram, new String[] {}));

		KThread.currentThread().finish();
	}

	/**
	 * Terminate this kernel. Never returns.
	 */
	public void terminate() {
		super.terminate();
	}

	/** Globally accessible reference to the synchronized console. */
	public static SynchConsole console;

	// dummy variables to make javac smarter
	private static Coff dummy1 = null;

	// book keep the availability of physical pages
	/*
	 * private HashSet availPages; int allocatePage(){ //numofpages known //this
	 * func only returns ppn for (each page from 0 to max of phy pages){
	 * if(availPages.contain(i)){ //test availPages.remove(i); //set return i; }
	 * else { //next page } } //use lock or semaphore }
	 * 
	 * freeOnePage(int ppn){ }
	 */

	// public int Translate(int vaddr){
	// final int pageSize = Processor.pageSize;
	// int vpn = vaddr % pageSize;
	// int ppn = pageTable[vpn];

	// }

	protected TranslationEntry[] pageTable;
	static HashMap<Integer, Integer> freePages = new HashMap<Integer, Integer>();
	final static Lock lock = new Lock();

}
