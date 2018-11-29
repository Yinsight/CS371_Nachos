package nachos.threads;

public class BridgeCrossing {

	private static int n_of_cars_on_bridge = 0;
	private static int[] n_of_cars_waiting;
	private static int cur_dir = -1;
	private static Lock lock;
	private static Condition[] cv;
	
	static class Bridge{
		
		public static boolean isSafe(int dir){
			if (cur_dir == -1) return true;
			else if (cur_dir != dir) return false;
				else if (n_of_cars_on_bridge < 3) return true;
					else return false;
		} 
		
		public Bridge(){
			cv = new Condition[2];
			cv[0] = new Condition(lock);
			cv[1] = new Condition(lock);
			
			n_of_cars_waiting = new int[2];
			n_of_cars_waiting[0] = 0;
			n_of_cars_waiting[1] = 0;
		}
		
		public static void enter(int dir){
			lock.acquire();
			n_of_cars_waiting[dir]++;
			while (!isSafe(dir)){
					cv[dir].sleep();
			}
			n_of_cars_waiting[dir]--;
			n_of_cars_on_bridge++;
			cur_dir = dir;
			lock.release();
		}
	
	public static void cross(int dir){
		lock.acquire();
		System.out.println("Vehicle crossing dir" + dir);
		lock.release();
	}
	
	public static void exit(int dir){
		lock.acquire();
		n_of_cars_on_bridge--;
		if (n_of_cars_on_bridge == 0) cur_dir = -1;
		if (n_of_cars_waiting[(dir+1)%2] > 0 ) cv[(dir+1)%2].wake();
		if (n_of_cars_waiting[dir] > 0 ) cv[dir].wake();
		lock.release();
	}
	}
	
		public static void selfTest() {
			
		KThread array[] = new KThread[10];
			
		for(int i=0;i<10;i++){
			
			KThread Vehicle = new KThread(new Runnable(){
				
						
			public void run() {
				
				Bridge.enter(1);
				Bridge.cross(1);
				Bridge.exit(1);	
			
			}
		}
		);
			Vehicle.fork();
			array[i]=Vehicle;
		}
		
		
		KThread array1[] = new KThread[10];
		
		for(int i=0;i<10;i++){
			
			KThread Vehicle = new KThread(new Runnable(){
			
			public void run() {
				
				Bridge.enter(0);
				Bridge.cross(0);
				Bridge.exit(0);	
			
			}
			
		}
		);
			Vehicle.fork();
			array1[i]=Vehicle;
		}
		
		
		for(int j=0;j<10;j++){
			array[j].join();
			array1[j].join();
		}
			
		    }
	}

