package edu.upenn.cis455.indexer.distributor;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import edu.upenn.cis455.paramaters.Params.MessageType;
import edu.upenn.cis455.revindex.ReverseIndex;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message; 
import rice.p2p.commonapi.Node; 
import rice.p2p.commonapi.NodeHandle; 
import rice.p2p.commonapi.Endpoint; 
import rice.p2p.commonapi.Application; 
import rice.p2p.commonapi.RouteMessage; 


public class IndexerApp implements Application { 

	IndexerNodeFactory nodeFactory; 
	Node node; 
	Endpoint endpoint; 
	
	public IndexerApp(IndexerNodeFactory nodeFactory, Node node) { 
		this.nodeFactory = nodeFactory; 
		this.node = node; 
		this.endpoint = node.buildEndpoint(this, "Ping Pong App"); 
		this.endpoint.register(); 
	} 


	public void sendMessageDirect(NodeHandle nh, String msgToSend, MessageType mt) { 
		OurMessage m = new OurMessage(node.getLocalNodeHandle(), msgToSend, mt); 
		endpoint.route( null, m, nh); 
	} 

	public void sendMessage(Id idToSendTo, String msgToSend, MessageType mt ) { 
		OurMessage m = new OurMessage(node.getLocalNodeHandle(), msgToSend, mt); 
		endpoint.route(idToSendTo, m, null); 
	} 

	public void deliver(Id id, Message message) { 

		System.out.println("IN DELIVER");
		OurMessage recievedMsg = (OurMessage) message;

		switch(recievedMsg.messageType) {
			case JOIN: 		if(node.getLocalNodeHandle() != recievedMsg.from) {
								System.out.println("RECEIVED CHARS REQUEST");
								String reply = null;
								reply = ReverseIndexCollectionNode.characters + "<>" + ReverseIndexCollectionNode.localAddr + "<>" + ReverseIndexCollectionNode.daemonPort;
								OurMessage replyMsg = new OurMessage(node.getLocalNodeHandle(), reply, MessageType.CHARS);
								endpoint.route(null, replyMsg, recievedMsg.from);
							}
							break;
						
			case CHARS:		System.out.println("GOT CHARACTERS" + recievedMsg.alpha);
							String[] info = recievedMsg.alpha.split("<>");
							String ip = info[1];
							int port = Integer.parseInt(info[2]);
							CharNode charNode = new CharNode(ip, port, recievedMsg.from);
							// retrieve the characters
							String[] temp = info[0].split("-");
							for(int i = temp[0].charAt(0); i<= temp[1].charAt(0) ; i++) {
								ReverseIndexCollectionNode.charMapping.put((char) i, charNode);
								System.out.println("charMapping: " + (char)i);
							}
							if(!ReverseIndexCollectionNode.socketMapping.containsKey(temp[0].charAt(0))) {
								Socket socket = null;
								PrintStream out = null;
								try {
									socket = new Socket(ip, port);
									out = new PrintStream(socket.getOutputStream());
								} catch (UnknownHostException e) {
									e.printStackTrace();
									return;
								} catch (IOException e) {
									e.printStackTrace();
									return;
								}
								for(int i = temp[0].charAt(0); i<= temp[1].charAt(0) ; i++) {
									ReverseIndexCollectionNode.socketMapping.put((char) i, out);
									System.out.println("socketMapping: " + (char)i);
								}
							}
							break;
				default: 	break;
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