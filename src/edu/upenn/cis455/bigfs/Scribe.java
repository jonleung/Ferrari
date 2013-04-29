package edu.upenn.cis455.bigfs;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scribe extends Worker {

	String nodeId;
	
	private BlockingQueue<Page> pagesQueue;
	private File bigFsDir;
	BigFile curBigDocFile;
	BigFile curBigUrlFile;
	
	int i_DELETETHIS=0;
	
	public Scribe(String nodeId, File bigFsDir, String s3BucketBaseName) {
		this.nodeId = nodeId;
		this.bigFsDir = bigFsDir;
		
		this.pagesQueue = new LinkedBlockingQueue<Page>();
		
		try {
			curBigDocFile = new BigFile("doc", bigFsDir, this.nodeId);
			curBigUrlFile = new BigFile("url", bigFsDir, this.nodeId);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		this.start();
	}
	
	public boolean enqueuePage(Page page) {
		return this.pagesQueue.offer(page);
	}
	
	
	protected void work() {
		Page page = null;
		try {
			page = pagesQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (BigFs.DEBUG) System.out.println(String.format("About to do writes for %s\t", page.url));
		
		writeBigDocFile(page.getDocEncoding());
		writeBigUrlFile(page.getUrlEncoding());

		if (BigFs.DEBUG) System.out.println();
		

	}
	
	private void writeBigUrlFile(String string) {
		try {
			curBigUrlFile.append(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (BigFs.DEBUG) {
			i_DELETETHIS++;
			System.out.println(String.format("URL Dequeued: %d: size = %f", i_DELETETHIS, curBigUrlFile.getMegabytes()));
		}

		if (this.curBigUrlFile.length() > curBigUrlFile.getLengthThreshold()) {
			BigFile oldBigUrlFile = curBigUrlFile;
			try {
				oldBigUrlFile.flush();
				curBigUrlFile = new BigFile("url", bigFsDir, this.nodeId);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
//			s3Uploader.upload(oldBigUrlFile);
		}
		else {
//			System.out.println(String.format("%d of %d\t%f MB", curBigUrlFile.length(),curBigUrlFile.getLengthThreshold(), curBigUrlFile.getMegabytes()));
		}
	}
	
	private void writeBigDocFile(String string) {
		try {
			curBigDocFile.append(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (BigFs.DEBUG) {
			i_DELETETHIS++;
			System.out.println(String.format("DOC Dequeued: %d: size = %f", i_DELETETHIS, curBigDocFile.getMegabytes()));
		}

		
		if (this.curBigDocFile.length() > curBigDocFile.getLengthThreshold()) {
			BigFile oldBigDocFile = curBigDocFile;
			try {
				oldBigDocFile.flush();
				curBigDocFile = new BigFile("doc", bigFsDir, this.nodeId);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
//			s3Uploader.upload(oldBigDocFile);
		}
		else {
//			System.out.println(String.format("%d of %d\t%f MB", curBigDocFile.length(),curBigDocFile.getLengthThreshold(), curBigDocFile.getMegabytes()));
		}
	}
	
	

	
	
	
	
	
	
	
}
