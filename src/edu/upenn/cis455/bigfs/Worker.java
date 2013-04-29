package edu.upenn.cis455.bigfs;

public abstract class Worker {

	Thread thread;
	
	public Worker() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					work();
				}
			}
		};
		
		thread = new Thread(runnable);
	}
		
	abstract protected void work();
	
	public void start() {
		thread.start();
	}

}
