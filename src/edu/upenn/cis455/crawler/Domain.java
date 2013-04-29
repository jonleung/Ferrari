package edu.upenn.cis455.crawler;

public class Domain {
	String DomainName;
	long accessTime;
	int count;
	
	Domain (String domain, long accessTime, int count) {
		this.DomainName = domain;
		this.accessTime = accessTime;
		this.count = count;
	}
}
