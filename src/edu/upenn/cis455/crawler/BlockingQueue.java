package edu.upenn.cis455.crawler;

import java.util.Vector;
 
public class BlockingQueue {
	
	private Vector<String> queue;
	
	public BlockingQueue() {
		queue = new Vector<String>();
	}
	
	synchronized public void emptyQueue(){
		queue.removeAllElements();
	}
	
	synchronized public void enqueue(String element) throws InterruptedException {
		// the queue full checking code goes here.
//		System.out.println("Adding to DNS unres. " + element);
		queue.add(element);	
		this.notify();			//RAJIB: why are we notifying everytime....shouldn't it be be when size become 1
	}
	
	public int size() {
		return queue.size();
	}
	
	synchronized public String dequeue() throws InterruptedException {
		String element = null;
		while (queue.isEmpty()){
			this.wait();
		}
		try {
			element = queue.remove(0);
		}
		catch (RuntimeException e) {
		    this.wait();
		}
		return element;
	}
}
