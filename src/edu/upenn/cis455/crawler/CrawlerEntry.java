package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.net.InetSocketAddress;

import rice.p2p.commonapi.Node;
import edu.upenn.cis455.bigfs.BigFs;
import edu.upenn.cis455.pastry.NodeFactory;
import edu.upenn.cis455.storage.BerkeleyDB;
import edu.upenn.cis455.storage.CrawledDocumentAccess;

public class CrawlerEntry {
	
	public static String getDomain(String URL) {
		String domain = URL;
		if(domain.contains("://")) {
			domain = domain.split("://")[1].trim();
		}
		if(domain.contains("/")) {
			domain = domain.substring(0, domain.indexOf('/'));
		}
		return domain;
	}
	
	public static void synchDB(){
		db.getEnv().sync();		
	}

	public static void main(String[] args) {
		
		if(args.length != 6) {
			System.out.println("Usage: CrawlEntry <local port> <bootstrap ip> " +
					"<bootstrap port> <seedURLFile> <bekeleydb_path> <max_doc_size_in_MB>");
		}
		
		int localPort = Integer.parseInt(args[0]);
		String bootstrapAddr = args[1];
		int bootstrapPort = Integer.parseInt(args[2]);
		InetSocketAddress bootstrapInetAddr = new InetSocketAddress(bootstrapAddr, bootstrapPort);
		String seedURLFilePath = args[3];
		String berkleyDBPath = args[4];
		MAX_DOC_SIZE = Integer.parseInt(args[5])*1024*1024;
		
		
		// initialize the DB.
		db = BerkeleyDB.getDBObject(berkleyDBPath);
		docAccess = new CrawledDocumentAccess(db.getEnv());
		
		// Initialize the node factory.
		NodeFactory nodeFactory = new NodeFactory(localPort, bootstrapInetAddr);
		Node node = nodeFactory.getNode();
		System.out.println("Created Node Factory");
		
		try {
			crawlManager = new Manager(NUM_THREADS);
			// Initialize the app.
			CrawlApp app = new CrawlApp(nodeFactory, node, crawlManager);
			System.out.println("Starting Crawl App");
			
			// now when the app has started, ask the manager to start their crawl workers.
			crawlManager.startCrawling(seedURLFilePath);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		// BIG FILESYSTEM		
		bigFs = new BigFs (node.getId().toStringFull(), "DefaultBucket");
	}

	static final int NUM_THREADS = 12;
	static Manager crawlManager;
	public static BerkeleyDB db;
	public static BigFs bigFs;
	public static CrawledDocumentAccess docAccess;
	static int MAX_DOC_SIZE = 2*1024*1024;
	
}
