package edu.upenn.cis455.aggregator;

import rice.p2p.commonapi.NodeHandle;

public class CharNode {

	public CharNode(String address, int port, NodeHandle nodeHande) {
		this.address = address;
		this.port = port;
		this.nodeHandle = nodeHande;
	}
	
	String address;
	int port;
	NodeHandle nodeHandle;
	
}
