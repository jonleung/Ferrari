package edu.upenn.cis455.aggregator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import rice.p2p.commonapi.Id;
import rice.pastry.PastryNode;
import edu.upenn.cis455.paramaters.Params.MessageType;
import edu.upenn.cis455.storage.BerkeleyDB;

public class AggregatorEntry {


	public static void main(String...args) {

		if(args.length < 6 || args.length > 7) {
			System.out.println("Usage: ReverseIndexCollectionNode <local ip> <local port> <bootstrap ip> " +
					"<bootstrap port> <daemon port> <bekeleydb_path> [startChar-endChar]");
			System.exit(0);
		}

		localAddr = args[0];
		int localPort = Integer.parseInt(args[1]);
		String bootstrapAddr = args[2];
		int bootstrapPort = Integer.parseInt(args[3]);
		InetSocketAddress bootstrapInetAddr = new InetSocketAddress(bootstrapAddr, bootstrapPort);
		daemonPort  = Integer.parseInt(args[4]);

		String berkleyDBPath = args[5];

		if(args.length == 7) {
			characters = args[6];
		} else if(args.length == 6){
			BOOT_NODE = true;
		}

		// initialize the DB.
		db = BerkeleyDB.getDBObject(berkleyDBPath);


		// Initialize the node factory.
		IndexerNodeFactory nodeFactory = new IndexerNodeFactory(localPort, bootstrapInetAddr);
		PastryNode node = (PastryNode) nodeFactory.getNode();
		System.out.println("Created Node Factory" + node.getLocalHandle());
		indexApp = new IndexerApp(nodeFactory, node);

		if (BOOT_NODE) {
			// this is the boot node
			createCharMap(nodeFactory);
			System.out.println("Both Map created");
			try {
				SearchHandler searchHandler = new SearchHandler(daemonPort);
				searchHandler.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} 
		else {
			// I am a word storage node. Wait for boot node
			// to give me words and I will store them.
			try {
				WordRequestHandler handler = new WordRequestHandler(daemonPort);
				handler.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createCharMap(IndexerNodeFactory nodeFactory) {

		while (charMapping.size() != 26) {

			Id randomID = nodeFactory.getIdFromString(Long.toString(System.currentTimeMillis()));
			System.out.println("Sending PING to " + randomID.toString());
			indexApp.sendMessage(randomID, "give", MessageType.JOIN);

			System.out.println("before timer");
			long waitfrom = System.currentTimeMillis();
			while((System.currentTimeMillis() - waitfrom) != 500) {
			}
			System.out.println("after timer");
		}
	}


	public static String localAddr;
	public static boolean start = false;
	public static String characters;
	public static IndexerApp indexApp;
	public static String dirname;
	public static int daemonPort;
	//	public static SendingApp sendingApp;
	public static boolean BOOT_NODE = false;
	public static int NUM_NODES = 2;
	public static HashMap<Character, CharNode> charMapping = new HashMap<Character, CharNode>();
//	public static HashMap<Character,PrintStream> socketMapping = new HashMap<Character, PrintStream>();
	public static BerkeleyDB db;
}
