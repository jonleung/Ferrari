package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import edu.upenn.cis455.storage.DomainInfo;
import edu.upenn.cis455.storage.DomainInfoAccess;
import edu.upenn.cis455.storage.URLSeenAccess;

public class DNSResolver {

	class Resolver extends Thread{

		DNSResolver dnsResolver;
		Manager manager;
		Resolver(DNSResolver dnsResolver, Manager manager) {
			this.dnsResolver = dnsResolver;
			this.manager = manager;
		}


		public void run() {
			while (true) {
				String urlString = null;
				try {
					urlString = dnsResolver.unResolvedQueue.dequeue();
					if(urlString.equals("") || urlSeen.isURLSeen(urlString))
						continue;
				} catch (InterruptedException e1) {
					System.out.println("DNS RESOLVER ERROR IN DEQUE");
				}

				URL url= null;
				try {
					url = new URL(urlString);
				} catch (MalformedURLException e) {
					System.err.println("MALFORMED URL: [" + urlString + "]");
				}
				urlSeen.putURL(urlString);
				String domain = url.getHost();
				if(!domainInfoAccess.isDomainSeen(domain)) {
					try {

						DomainInfo domainInfo = new DomainInfo();
						domainInfo.domain = domain;
						//						domainInfo.address = InetAddress.getByName(domain);
						ArrayList<String> robotValues = RobotParser.parseRobot(domain);
						domainInfo.crawlDelay = Float.parseFloat(robotValues.get(0));
						for(int i = 1; i < robotValues.size(); i++){
							domainInfo.disallow.add(robotValues.get(i));
						}
						domainInfoAccess.putDomain(domainInfo);

						manager.addToPendingURLs(domain, urlString);

						String domainURL =  "http://"+domain;
						if(!urlSeen.isURLSeen(domainURL)) {
							manager.addToPendingURLs(domain, "http://"+domain);
						}
						manager.jobQueue.add(new Domain(domain, System.currentTimeMillis(),1));
					} catch (Exception e) {
						System.out.println("PROBLEM IN ROBOT IN DNS RESOLEVER");
					}
				} else {

					manager.addToPendingURLs(domain, urlString);
				}
			}
		}
	}

	public DNSResolver(Manager manager) {
		//		domainsSeen = new HashSet<String>();
		//urlSeen = new HashSet<String>();
		unResolvedQueue = new BlockingQueue();

		// start the resolvers.
		for (int i = 0; i < resolvers.length; i++){
			resolvers[i] = new Resolver(this, manager);
			resolvers[i].start();
		}
	}

	URLSeenAccess urlSeen = new URLSeenAccess(CrawlerEntry.db.getEnv());
	DomainInfoAccess domainInfoAccess = new DomainInfoAccess(CrawlerEntry.db.getEnv());
	BlockingQueue unResolvedQueue;
	Resolver[] resolvers = new Resolver[10]; //number of Resolvers = 2.

}
