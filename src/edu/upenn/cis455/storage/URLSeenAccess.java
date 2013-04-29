package edu.upenn.cis455.storage;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class URLSeenAccess {
	private EntityStore store;
	private PrimaryIndex<String, URLSeen> urlIndex;
	private Environment environment;

	public URLSeenAccess(Environment env){
		environment = env;
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		store = new EntityStore(env, "URLStore", storeConfig);
		urlIndex = store.getPrimaryIndex(String.class, URLSeen.class); 
	}

	public void putURL(String url) {
		URLSeen urlSeen = new URLSeen(url);
		urlIndex.put(urlSeen);
		environment.sync();
	}

	public boolean isURLSeen(String url) {
		if (urlIndex.get(url)!=null) 
			return true;
		else
			return false;
		
	}

	public void closeStore(){
		store.close();
	}
}
