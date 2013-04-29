package edu.upenn.cis455.crawler;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.tidy.Tidy;
import com.sleepycat.je.UniqueConstraintException;

import edu.upenn.cis455.bigfs.Page;
import edu.upenn.cis455.storage.CrawledDocument;
import edu.upenn.cis455.storage.CrawledDocumentAccess;
import edu.upenn.cis455.storage.DomainInfo;


// The worker thread is responsible for doing the work of crawling in the 
// the web.
public class Worker  {

	// Constructor
	public Worker(final Manager manager, int threadID) {
		m_manager = manager;
		m_workerID = threadID;
	}
	
	// this function finds out if a url belongs to one of the disallow 
	public boolean isURLAllowed(DomainInfo domainInfo, String url){
		if (url == null)
			return false;
		String reqURI;		
		String[] parts = url.split(domainInfo.domain);
		if(parts.length == 1) {
			reqURI = new String("/");
		}
		else {
			reqURI = parts[1].trim();
		}
		for(String str : domainInfo.disallow){
//			String str = domainInfo.disallow.get(i);
			// handle '*'
//			if(str.contains("*")){
//				if(reqURI.startsWith(str.substring(0, str.indexOf('*')))) {
//					return false;
//				}
//			}			
			if(reqURI.startsWith(str)){
				return false;
			}
		}
		return true;
	}

	public void crawl(DomainInfo domainInfo, String url) {		
	
		if(url == null || !isURLAllowed(domainInfo, url)) {
			System.out.println("disallowed: " + url);
			System.err.println("URL NOT ALLOWED");
			return;
		}
		
		System.out.println("THREAD[" + m_workerID + "] Crawling:URL:" + url);
		
		Document doc = null;
		
		HashMap<String, String> headerMap = null;
		try {
			headerMap = docFetcher.doHEAD(url);
		} catch (IOException e) {
			System.err.println("ERROR IN DOHEAD REQUEST " + e.getMessage());
		}
		if(headerMap == null) {
			System.out.println("\nempty doHead Return");
			return;
		}
		
		if(!headerMap.get("content-type").equals("html") 
				&& !headerMap.get("content-type").equals("xml")) {
//			System.out.println("content type: " + headerMap.get("content-type"));
			return;
		}		

		boolean isXML = headerMap.get("content-type").equals("xml");
		CrawledDocument crawledDoc = new CrawledDocument();
		crawledDoc = docAccess.getCrawledDocument(url);
		if(crawledDoc != null) {
			return;
		}

		// we have come here because either the database has no entry of this 
		// url the document has changed.
		// We will have to crawl. Sadly.
		
		int contentLength = Integer.parseInt(headerMap.get("content-length"));
		if(contentLength > CrawlerEntry.MAX_DOC_SIZE) {
//			System.out.println("MAX length reached");
			return;
		}

		
		try {
			doc = Jsoup.connect(url).userAgent("cis455crawler").header("Accept-Language", "en-US").timeout(5000).get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		

		// get the SHA1 of the message.
		String shaDoc = null;
		String shaUrl = null;
		try {
			shaDoc = getSHA1(doc.text());
			shaUrl = getSHA1(url);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("ERROR IN SHA1");
		} catch (NoSuchProviderException e) {
			System.err.println("ERROR IN SHA1");
		}

		// now we try to put the hash in the database.
		try {
			docAccess.putCrawledDocument(new CrawledDocument(url, System.currentTimeMillis(), shaDoc, isXML));
		}
		catch(UniqueConstraintException uce){
			// the secondary key already exists. So the site is mirror? OMG what a deception.
			uce.printStackTrace();
			return;				
		}
		
		List<String> urls = null;
		if(!isXML) {
//			System.out.println("Sending on pastry");
			urls = extractHrefsAndEnqueue(url, doc);
		}
		
//		System.out.println("Start Writing to BIG FS");
		Page page = new Page();
		page.setUrl(url);
		page.setDocumentId(shaUrl);
		page.setHeaders(headerMap.get("headersString"));
		page.setHtml(doc.html());
		page.setUrls(urls);
//		System.out.println("WRITING TO BIGFS: " + url);
		CrawlerEntry.bigFs.write(page);
		
//		System.out.println("DONE Writing to BIG FS");
		
	}

	// this function extracts all the hrefs from a document and enqueues them
	// to the threads who are responsible for the domain.
	public List<String> extractHrefsAndEnqueue(String pageURL, Document doc) {

		Elements linkElements = doc.select("a");
		List<String> links = new ArrayList<String>();
		for (Element link : linkElements) {
			String url = link.attr("abs:href");
			links.add(url);
		}
		
		for(int i = 0; i < links.size() && m_manager != null; i++) {
			m_manager.sender.sendToPastry(links.get(i));
//			m_manager.app.sendMessage(m_manager.app.nodeFactory.getIdFromString(),
//					links.get(i));
		}
		return links;
	}

	public CrawledDocumentAccess getCrawldbAccess() {
		//docAccess = new CrawledDocumentAccess(env);
		return docAccess;
	}

	// this function computes SHA1 of the message (in for of String) passed to it.
	public String getSHA1(String fileContentStr) throws NoSuchAlgorithmException, NoSuchProviderException{
		byte[] fileContent = fileContentStr.getBytes();
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		messageDigest.update(fileContent);
		byte[] digestBuffer = messageDigest.digest();
		return new String(digestBuffer);		
	}

	// the data structure that contains the entire information about the domain.
	Manager m_manager;
	int m_workerID;
	DocumentFetcher docFetcher = new DocumentFetcher();
	CrawledDocumentAccess docAccess = new CrawledDocumentAccess(CrawlerEntry.db.getEnv());
	final int WAIT_TIMEOUT = 10000;
}

