package edu.upenn.cis455.aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.upenn.cis455.revindex.Hit;
import edu.upenn.cis455.revindex.HitScoreComparator;
import edu.upenn.cis455.revindex.ReverseMapping;

public class SearchHandler extends Thread {

	public SearchHandler(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	public void run() {

//		while(true) {
			Socket socket = null;
			String keywords = null;
			try {
//				socket = serverSocket.accept();

				keywords = "aaaaaa";
				// read the keyword request from web
//				keywords = getKeyword(socket);
				
				String reply = getTheReplyToReturn(keywords);
				System.out.println("RESULT : " + reply);
//				PrintStream out = new PrintStream(socket.getOutputStream());
//				out.println(reply);

				// TODO: keywords can be multi-word ... take care of that
				//	String[] keys = keywords.split(" ");

			} catch (Exception e) {
				e.printStackTrace();
			}
//	s	}

	}

	public String getTheReplyToReturn(String keywords) {
		
		SearchClient searchClient = new SearchClient();
		String result = null;
		try {
			result = searchClient.getStoredResults(keywords);
			ReverseMapping resultMap = ReverseMapping.deserialize(result);
			
			HashMap<String, Hit> urlHitMap = resultMap.hits;
			System.out.println("HITS SIZE: " + urlHitMap.size());

			//TreeSet<Hit> hitDecOrder = new TreeSet<Hit>(new HitScoreComparator());
			//for(Hit hit:urlHitMap.values()){
			//hitDecOrder.add(hit);
			//}

			List<Hit> sortedList = new ArrayList<Hit>(urlHitMap.values());
			Collections.sort(sortedList, new HitScoreComparator());

			StringBuilder JSONreply = new StringBuilder();
			JSONreply.append("{ results : [");
			for(int i=0;i<sortedList.size();i++) {
			JSONreply.append("{ url: \"" + sortedList.get(i).url +"\" },");
			}
			JSONreply.append("] }");
			System.out.println("JSONREPLY: " + JSONreply.toString());
			
			//wl.sendResponse(keyword, JSONreply.toString());
			return JSONreply.toString();
			
		} catch (UnknownHostException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	//TODO:  Modify this get keyword according to the format provided by JON
	String getKeyword(Socket socket) throws IOException {
		String keyword = null;

		InputStreamReader reader = new InputStreamReader(socket.getInputStream());
		BufferedReader in = new BufferedReader(reader);

		String line = in.readLine();
		String request = line.split(" ")[1].trim();

		if(request.contains("/search?q=")) {
			keyword = request.split("=")[1].trim();
		}

		return keyword.toLowerCase();
	}	
	public ServerSocket serverSocket;
}
