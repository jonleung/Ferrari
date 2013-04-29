package edu.upenn.cis455.storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class PendingURLs {
	
	public PendingURLs(String domain){
		this.domain = domain;
	}

	public PendingURLs(){
		
	}

	@PrimaryKey
	public String domain;
	public Set<String> urlSet= new HashSet<String>();
}
