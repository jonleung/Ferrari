package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class URLSeen {
	
	public URLSeen(String url){
		this.url = url;
	}

	public URLSeen(){
		
	}

	@PrimaryKey
	public String url;

}
