package edu.upenn.cis455.storage;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class DomainInfo {
	
	public DomainInfo(String domain, float crawlDelay, Set<String> disallow){
		this.domain = domain;
		this.crawlDelay = crawlDelay;
		this.disallow = disallow;
	}

	public DomainInfo(){
		
	}

	@PrimaryKey
	public String domain;
	
	public float crawlDelay;
	public Set<String> disallow = new HashSet<String>(); 

}
