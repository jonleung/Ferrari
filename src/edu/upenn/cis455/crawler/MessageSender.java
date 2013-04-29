package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import edu.upenn.cis455.crawler.DNSResolver.Resolver;

// This thread sends out messages in the pastry ring 
// in a controlled fashion so that it doesn't flood the pastry 
// ring.
public class MessageSender {
	
	
	public class MessageSenderWorker extends Thread {
		
		final int CRAWL_DEPTH = 10;
		MessageSender messageSender;
		DocumentFetcher docFetcher = new DocumentFetcher();
		
		MessageSenderWorker(MessageSender messageSender) {
			this.messageSender = messageSender;
		}
		
		// this function takes the content type and returns if we are doing 
		// this content or not.
		boolean handleContentType(String contentType){
//			System.out.println("CONTENT TYPE " + contentType);
			if (contentType == null) {
				// we dont know the content type. Can't what it is. So return true.
				return true;
			}
			
			if(contentType.contains("text/plain") ||
					contentType.contains("text/html") ||
					contentType.contains("text/xml") ||
					contentType.contains("application/xml")) {
				return true;
			}
			return false;
		}
		
		// this function checks whether we can enqueue this URL or 
		// not. If yes it enqueues the URL and returns a boolean true
		// If no, it returns a boolean false.	
		public void run(){
			String urlString = null;
			int waitcount = 0;
			while(true){
				try {
					urlString = messageSender.urlQueue.dequeue();
					
					if(urlString.equals("")){
						continue;
					}					
					
					// make a head request now.
					HttpURLConnection urlConn = (HttpURLConnection) new URL(urlString).openConnection();
					urlConn.setRequestMethod("HEAD");
					
					if(urlConn.getResponseCode() == HttpURLConnection.HTTP_OK &&
						handleContentType(urlConn.getContentType())) {
						int depth  = StringUtils.countMatches(urlString, "/") - 2; // two slashes for 'http://'.
						if(depth < CRAWL_DEPTH) { 
							String domain = CrawlerEntry.getDomain(urlString);
							crawlApp.sendMessage(crawlApp.nodeFactory.getIdFromString(domain), urlString);
						}
						if(waitcount++ == 500){
							wait(WAIT_TIME);
							waitcount = 0;
						}
						
					}
					else {
						// test code 
						System.out.println("skipping the URL " + urlString);
					}
				
				} catch (MalformedURLException e) {
					System.err.println(" MALFORMED URL: " + urlString);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (IOException e2){
					e2.printStackTrace();
				}
				catch (Exception e){
					e.printStackTrace();
				}			
			}
		}	
	}
	
	
	MessageSender(CrawlApp app){
		this.crawlApp = app;
		
		for (int i = 0; i < messageSenderWorker.length; i++){
			messageSenderWorker[i] = new MessageSenderWorker(this);
			messageSenderWorker[i].start();
		}
	}
	
	// NOTE: calling this URL doesnt send the message immediately.
	boolean sendToPastry(String url){
		try {
			urlQueue.enqueue(url);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}			
	}
		
	CrawlApp crawlApp;
	// TODO: make it persistent.
	BlockingQueue urlQueue = new BlockingQueue();	
	final int WAIT_TIME = 1000;  //3 seconds 	
	final int NUM_SENDER_THREADS = 5;
	MessageSenderWorker[] messageSenderWorker = new MessageSenderWorker[NUM_SENDER_THREADS];
}
