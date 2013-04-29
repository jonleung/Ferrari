package edu.upenn.cis455.indexer.distributor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import edu.upenn.cis455.crawler.BlockingQueue;
import edu.upenn.cis455.paramaters.Params;

public class SendToPastryPool {

	public class PoolThread extends Thread {
		SendToPastryPool stpp;

		PoolThread (SendToPastryPool stpp) {
			this.stpp = stpp;

		}

		public void run() {
			String filename = null;
			PrintStream out = null;
			while (true) {

				try {
					filename = stpp.fileToRead.dequeue();
					System.out.println("Successfully dequeued");
					DataInputStream dis = new DataInputStream(new FileInputStream(filename));
					BufferedReader br = new BufferedReader(new InputStreamReader(dis));
					String line = null;
					line = br.readLine();
					while(line != null) {
						
						String word = line.split("\t", 2)[0].trim();
						System.out.println("lin:" + line);
						System.out.println("word: " + word);
						
						// sending it to the appropriate node
						out = ReverseIndexCollectionNode.socketMapping.get(word.charAt(0));
						out.println(line);
						out.flush();
						
						//reading next line
						synchronized(this) {
							try {
								wait(1);
							} catch (InterruptedException e) { }
						}
						line = br.readLine();
					}
					br.close();
				} catch (InterruptedException e) {
					System.err.println("ERROR IN READING FILE");
				} catch (FileNotFoundException e) {
					System.err.println("FILE NOT FOUND");
				} catch (IOException e) {
					System.err.println("IO EXCEPTION");
				}
			}
		}

	}

	public SendToPastryPool() {
		for(int i=0; i<poolThreads.length; i++) {
			poolThreads[i] = new PoolThread(this);
		}
		
		for(PoolThread pt : poolThreads) {
			pt.start();
		}
	}

	public BlockingQueue fileToRead = new BlockingQueue();
	public PoolThread[] poolThreads = new PoolThread[Params.SendToPastryPoolThreadCount];
}
