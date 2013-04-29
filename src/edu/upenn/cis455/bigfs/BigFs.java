package edu.upenn.cis455.bigfs;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
	// At the startup of the crawl node (not on start and restart but on first turned on, this should be set)
	DateFormat dateFormat = new SimpleDateFormat("MMMMM-d_h:mma");
	Date date = new Date();
	this.awsBucketName = dateFormat.format(date);
*/

public class BigFs {
	
	static final boolean DEBUG = true;

	private Scribe scribe;
	private File parentBigFsDir;
	private File curBigFsSubDir;
	
	public BigFs(String nodeId, String s3BucketBaseName) {	
		
		// Ensure "/bigfs"
		this.parentBigFsDir = new File("bigfs");
	  if (!this.parentBigFsDir.exists()) {
	  	this.parentBigFsDir.mkdir();
	  }
	  
	// Create Crawl Folder
	  Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("MMMMdd_hh-mm-ssa");
	  String curDirName = String.format("%s_crawl", dateFormat.format(date));	  		
	  this.curBigFsSubDir = new File(parentBigFsDir.getPath(), curDirName);
	  if (!this.curBigFsSubDir.exists()) {
	  	this.curBigFsSubDir.mkdir();
	  }
		this.scribe = new Scribe(nodeId, curBigFsSubDir, s3BucketBaseName);		
	}
	
	public void write(Page page) {
		if (!page.valid()) {
			if (BigFs.DEBUG) System.out.println(String.format("Page NOT enqueued because the page is invalid because all the variables were not set."));
			throw new RuntimeException();
		}
		this.scribe.enqueuePage(page);
		if (BigFs.DEBUG) System.out.println(String.format("Page for %s successfully enqueued.", page.url));
	}
		
	public static void main (String[] args) {
		Page page = new Page();
		page.setDocumentId("55b0096f913f500a36b74457f1a7a93182bbd9b384b9e6546daa05af493cd269");
		page.setUrl("http://helloworld.com");
		page.setHeaders("header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2header1:value1\nheader2:value2");
		page.setHtml("<h1>Hello World Hello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello WorldHello World</h1>");
		page.urls = new ArrayList<String>();
		page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); page.urls.add("http://google.com"); page.urls.add("http://yahoo.com"); page.urls.add("http://bing.com"); 
		
		BigFs bigFs = new BigFs("1", "BigFs");
		int max = 10;
		for (int i=0; i<max; i++) {
			bigFs.write(page);
		}
	}	
	
}
