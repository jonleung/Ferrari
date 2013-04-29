package edu.upenn.cis455.crawler;
import org.mpisws.p2p.transport.priority.PriorityTransportLayer;

import edu.upenn.cis455.pastry.NodeFactory;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.RouteMessage;
import rice.pastry.PastryNode;
import rice.pastry.socket.SocketPastryNodeFactory;

public class CrawlApp implements Application {
	NodeFactory nodeFactory;
	PastryNode node;
	Endpoint endpoint;
	Manager manager;
	PriorityTransportLayer priority;


	public CrawlApp(NodeFactory nodeFactory, Node node, Manager manager) {
		this.nodeFactory = nodeFactory;
		this.node = (PastryNode) node;
		this.manager = manager;
		this.endpoint = node.buildEndpoint(this, "Crawler App");
		this.endpoint.register();
		this.manager.registerApp(this);
		//this.dnsResolver = new DNSResolver(manager);		
	}

	public void sendMessage(Id idToSendTo, String msgToSend) {
//		System.out.println("SENDING MESSAGE");
		CrawlMessage m = new CrawlMessage(node.getLocalNodeHandle(), msgToSend , null);
		endpoint.route(idToSendTo, m, null);
	}


	// TODO: should put the url in crawl queue/dns resolver.
	public void deliver(Id id, Message message) {	
		CrawlMessage msg = (CrawlMessage) message;
//		System.out.println("CRAWL APP DELIVER");
		if(msg.key != null) {

//			if(msg.key.equals("PING")){
//				System.out.println("Received PING to ID " + id.toString() + " from node " + msg.from + "; returning PONG");
//				CrawlMessage replyMessage = new CrawlMessage(node.getLocalNodeHandle(), "PONG", "PONG");
//				endpoint.route(null, replyMessage, msg.from);
//			}
//
//			else if(msg.key.equals("PONG")){
//				System.out.println("Received PONG from node " + msg.from.getId().toString());
//			}
//			else {
//				System.out.println("GOT in Deliver " + msg.key);
				try {
					manager.dnsResolver.unResolvedQueue.enqueue(msg.key);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//			}
		}
	}

	public void update(NodeHandle handle, boolean joined) {
		// This method will always be empty in your assignment
	}
	public boolean forward(RouteMessage routeMessage) {
		// This method will always return true in your assignment
		return true;
	}

}