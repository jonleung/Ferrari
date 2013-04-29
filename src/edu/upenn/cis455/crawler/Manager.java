package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis455.storage.DomainInfo;
import edu.upenn.cis455.storage.DomainInfoAccess;
import edu.upenn.cis455.storage.PendingURLs;
import edu.upenn.cis455.storage.PendingURLsAccess;

public class Manager {

	public class WorkerThread extends Thread {
		public Worker m_worker;
		DomainInfoAccess accessD;
		PendingURLsAccess accessPenndingURL;
		// the user thread that this thread will get attached to.
		public WorkerThread(int threadID, final Manager manager) {
			m_worker = new Worker(manager, threadID);
			accessD = new DomainInfoAccess(CrawlerEntry.db.getEnv());
			accessPenndingURL = new PendingURLsAccess(CrawlerEntry.db.getEnv());
		}

		public void run() {
			PendingURLs pendingURL;
			while(true) {
				try {
					
//					System.out.println("job queue size: " + jobQueue.size());
					String url;
					Domain domain = jobQueue.take();
					DomainInfo domainInfo = accessD.getDomainInfo(domain.DomainName);
							
					System.out.println("Worker Thread " + m_worker.m_workerID + " domain: " + domain.DomainName);
					long waitTime = domain.accessTime - System.currentTimeMillis();
					if(waitTime > 0) {
						wait(waitTime);
					}
					pendingURL = accessPenndingURL.getURL(domain.DomainName);
					if (pendingURL.urlSet.size() >=1 ) {
						
						url = pendingURL.urlSet.iterator().next();
						pendingURL.urlSet.remove(url);
						accessPenndingURL.putPendingURL(pendingURL);
						
						m_worker.crawl(domainInfo, url);
						domain.accessTime = domain.accessTime + ((long) domainInfo.crawlDelay*1000);
						domain.count++;
						//System.out.println("CRAWL DELAY " + domainInfo.crawlDelay + " for domain " + domainInfo.DomainName);
						jobQueue.add(domain);
					}
					else {
						System.out.println("domain has no url");
					}
			
				} catch (Exception e) {
					
				}
			}
		}
	}

	public Manager(int numThreads) {
		this.numThreads = numThreads;
		workerThreads = new WorkerThread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			workerThreads[i] = new WorkerThread(i, this);
		}
		dnsResolver = new DNSResolver(this);
	}

	public void startCrawling(String seedURLFilePath) throws InterruptedException, IOException {
		File file = new File(seedURLFilePath);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String seedUrl;
		
		for (int i = 0; i < numThreads; i++) {
			workerThreads[i].start();
			System.out.println("starting thread " + i);
		}
		
		EntityCursor<PendingURLs> cursor = accessPendingURL.getPendingURLCursor();
		boolean firstTime = true;
		
		for(PendingURLs url : cursor) {
			firstTime = false;
			jobQueue.add(new Domain(url.domain, System.currentTimeMillis(),1));
		}
		
		if(firstTime) {
			System.out.println("First time run");
			while((seedUrl = br.readLine()) != null) {
				app.sendMessage(app.nodeFactory.getIdFromString(seedUrl.trim()), seedUrl.trim());
			}
		} 
		cursor.close();
		br.close();
	}

	public void addToPendingURLs(String domain, String URL) {
		try {
			PendingURLs pendingURLs = accessPendingURL.getURL(domain);
			if( pendingURLs == null) {
				pendingURLs = new PendingURLs(domain);
			}
			pendingURLs.urlSet.add(URL);
			accessPendingURL.putPendingURL(pendingURLs);
		} catch (Exception e) {
			System.err.println("ERROR IN ADDING TO PENDING URL MAP");
		}
	}


	void registerApp(CrawlApp app){
		this.app = app;
		// start the sender too.
		sender = new MessageSender(app);
	}

	DNSResolver dnsResolver;
	private WorkerThread[] workerThreads;
	int numThreads;
	CrawlApp app;
	//ConcurrentHashMap<String, BlockingQueue> pendingURLs = new ConcurrentHashMap<String, BlockingQueue>();
	PendingURLsAccess accessPendingURL = new PendingURLsAccess(CrawlerEntry.db.getEnv());
	Comparator<Domain> domainInfoComparator = new DomainComparator();
	PriorityBlockingQueue<Domain> jobQueue = new PriorityBlockingQueue<Domain>(100, domainInfoComparator);
	MessageSender sender;
}
