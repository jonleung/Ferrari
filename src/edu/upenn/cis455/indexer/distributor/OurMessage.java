package edu.upenn.cis455.indexer.distributor;

import edu.upenn.cis455.paramaters.Params.MessageType;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;


@SuppressWarnings("serial")
public class OurMessage implements Message {

	
	public OurMessage(NodeHandle from, String alpha, MessageType messageType) {
		this.messageType = messageType;
		this.from = from;
		this.alpha = alpha;
	}
	
	@Override
	public int getPriority() {
		return MAX_PRIORITY;
	}

	
	public NodeHandle from;
	public String alpha;
	public MessageType messageType; 
	
}
