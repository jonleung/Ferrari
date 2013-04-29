package edu.upenn.cis455.indexer.distributor;

import java.io.File;

public class ReverseIndexDistributer {

	public void distribute(String dirname) {

		SendToPastryPool stpp = new SendToPastryPool();
		File dir = new File(dirname);
		for (File filename : dir.listFiles()) {
			try {
				System.out.println("Enqueueing: " + filename.getAbsolutePath());
				stpp.fileToRead.enqueue(filename.getAbsolutePath());
			} catch (InterruptedException e) {
				System.err.println("Error in reading directory");
			}
		}
		
		// TODO: close all sockets here
	}
}

// TODO: recursively call this function if there is a directory inside a direcoty.