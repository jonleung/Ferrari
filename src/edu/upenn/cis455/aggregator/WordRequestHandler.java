package edu.upenn.cis455.aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis455.revindex.ReverseIndex;

// This class would do the following:-
// (1) Being a Socket server, create a socket connection with the boot node
// (2) Keep the socket open for ever.
// (3) Store the messages containing reverse indices from Boot in Redis.

public class WordRequestHandler  extends Thread{
	public WordRequestHandler(int portNumber) throws IOException{
		this.m_serverSocket = new ServerSocket(portNumber);
	}

	public void run() {

		Socket socket = null;
		InputStreamReader reader = null;
		BufferedReader in = null;
		PrintStream out = null;
		System.out.println("Waiting for socket connection...");
		
		while(true){			
			try {
				
				socket = m_serverSocket.accept();
				System.out.println("Got Connection.");		
				reader = new InputStreamReader(socket.getInputStream());
				in = new BufferedReader(reader);
				out = new PrintStream(socket.getOutputStream());
				
				
				String word = in.readLine();
				System.out.print("RECEIVED " + word);
				String reply = ReverseIndex.retrieveSerializedReverseMapping(word);
				System.out.println("REPLYING " + reply);
				out.println(reply);

			} catch (SocketException se){
				System.out.println("Socket Exception");
				this.stop();
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
			} 			
		}				
	}

	ServerSocket m_serverSocket;

}
