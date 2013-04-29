package edu.upenn.cis455.storage;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class PendingURLsAccess {
	private EntityStore store;
	private PrimaryIndex<String, PendingURLs> domainIndex;
	private Environment environment;

	public PendingURLsAccess(Environment env){
		environment = env;
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		store = new EntityStore(env, "PendingURLsStore", storeConfig);
		domainIndex = store.getPrimaryIndex(String.class, PendingURLs.class); 
	}

	public void putPendingURL(PendingURLs url) {
		domainIndex.put(url);
		environment.sync();
	}

	public PendingURLs getURL(String domain) {
		return domainIndex.get(domain);
	}
	
	public void deletePendingURLs(String domain) {
		domainIndex.delete(domain);
		environment.sync();
	}

	public EntityCursor<PendingURLs> getPendingURLCursor(){
		return domainIndex.entities();
	}
	
	public void closeStore(){
		store.close();
	}
}
