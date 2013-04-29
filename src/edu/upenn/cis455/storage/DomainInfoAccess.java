package edu.upenn.cis455.storage;

import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class DomainInfoAccess {
	private EntityStore store;
	private PrimaryIndex<String, DomainInfo> domainIndex;
	private Environment environment;

	public DomainInfoAccess(Environment env){
		environment = env;
		StoreConfig storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		store = new EntityStore(env, "DomainStore", storeConfig);
		domainIndex = store.getPrimaryIndex(String.class, DomainInfo.class); 
	}

	public void putDomain(DomainInfo domainInfo) {
		domainIndex.put(domainInfo);
		environment.sync();
	}

	public DomainInfo getDomainInfo(String domain) {
		return domainIndex.get(domain);
	}
	
	public boolean isDomainSeen(String domain) {
		if(getDomainInfo(domain)  != null) 
			return true;
		else 
			return false;
	}

	public void closeStore(){
		store.close();
	}
}
