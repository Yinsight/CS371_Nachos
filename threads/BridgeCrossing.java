package nachos.threads;

public class BridgeCrossing {

	private int n_of_cars_on_bridge = 0;
	private int[] n_of_cars_waiting;
	private int cur_dir = -1;
	private Lock lock;
	private Condition[] cv;
	
	
	class Bridge{
		
		public boolean isSafe(int dir){
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
		
		public void enter(int dir){
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
	}
	public void cross(){
		lock.acquire();
		System.out.println("Vehicle crossing in" + cur_dir + "direction.");
		lock.release();
	}
	public void exit(int dir){
		lock.acquire();
		n_of_cars_on_bridge--;
		if (n_of_cars_on_bridge == 0) cur_dir = -1;
		if (n_of_cars_waiting[(dir+1)%2] > 0 ) cv[(dir+1)%2].wake();
		if (n_of_cars_waiting[dir] > 0 ) cv[dir].wake();
		lock.release();
	}
}
