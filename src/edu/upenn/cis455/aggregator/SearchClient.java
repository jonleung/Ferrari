package edu.upenn.cis455.aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import edu.upenn.cis455.indexer.Stemmer;

public class SearchClient {

	public SearchClient() {

	}

	// gets the search results from the pastry ring.
	public String getStoredResults(String keyword) throws UnknownHostException, IOException{

		Stemmer stemmer = new Stemmer();
		stemmer.add(keyword.toLowerCase().toCharArray(), keyword.length());
		stemmer.stem();
		String stemmedKeyword = stemmer.toString();
		
		String ipAddress = AggregatorEntry.charMapping.get(stemmedKeyword.charAt(0)).address;
		int port =  AggregatorEntry.charMapping.get(stemmedKeyword.charAt(0)).port;

		// make a socket connection to this address
		PrintWriter out = null;
		BufferedReader in = null;
		Socket socket = null;
		System.out.println("PORT :" +port);
		socket = new Socket(ipAddress, port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// now its time to send the request.
		System.out.println("SENDING REQ: " + stemmedKeyword);
		out.println(stemmedKeyword);
		out.flush();
		
		// RESPONSE 
		// the response will come now.
		String wordResults = in.readLine();
		out.close();
		in.close();
		socket.close();	
		return wordResults;
	}
}
