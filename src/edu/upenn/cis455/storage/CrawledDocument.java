package edu.upenn.cis455.storage;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class CrawledDocument {
	public CrawledDocument(String url, long lastAccessedTime,
			String sha1, boolean isxml){
		this.url = url;
		this.lastAccessTime = lastAccessedTime;
		this.contentSha1 = sha1;
		this.isxml = isxml;
	}
	public CrawledDocument(){
	}
	
	/**
	 * @param args
	 */
	
	@PrimaryKey
	public String url;
	
	@SecondaryKey(relate = Relationship.ONE_TO_ONE)
	public String contentSha1;	
	public long lastAccessTime;
	public boolean isxml;	
}
