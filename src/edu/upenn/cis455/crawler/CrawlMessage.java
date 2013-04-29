package edu.upenn.cis455.crawler;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

@SuppressWarnings("serial")
public class CrawlMessage implements Message {
	
	public CrawlMessage(NodeHandle from, String key, String content) {
		this.from = from;
		this.key = key;
		this.content = content;
	} 
	
	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}
	
	NodeHandle from;
	String content; 
	String key;
	boolean wantResponse = true;
//	public MessageAction action = MessageAction.NOT_DEFINED;
}
