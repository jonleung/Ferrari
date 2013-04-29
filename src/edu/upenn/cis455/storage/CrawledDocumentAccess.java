package edu.upenn.cis455.storage;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class CrawledDocumentAccess {

	private EntityStore store;
	private PrimaryIndex<String, CrawledDocument> documentIndex;
	private Environment environment;

	// this constructor initialized/configures the storage 
	// for Documents table.
	public CrawledDocumentAccess(Environment env){
		environment = env;
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		store = new EntityStore(env, "DocumentStore", storeConfig);
		documentIndex = store.getPrimaryIndex(String.class, CrawledDocument.class); 
	}

	public void putCrawledDocument(CrawledDocument d) {
		documentIndex.put(d);
		environment.sync();
	}

	public CrawledDocument getCrawledDocument(String uniqueID) {
		return documentIndex.get(uniqueID);
	}
	
	
	public String getID() {
		String id = null;
		
		return id;
	}
	public EntityCursor<CrawledDocument> getCrawledDocumentCursor(){
		return documentIndex.entities();
	}

	public void deleteCrawledDocument(String uniqueID) {
		documentIndex.delete(uniqueID);
		environment.sync();
	}

	public void closeStore(){
		store.close();
	}
}
