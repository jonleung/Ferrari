package edu.upenn.cis455.indexer.distributor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.upenn.cis455.revindex.ReverseIndex;

// This class would do the following:-
// (1) Being a Socket server, create a socket connection with the boot node
// (2) Keep the socket open for ever.
// (3) Store the messages containing reverse indices from Boot in Redis.
public class WordStorageHandler  extends Thread{
	public WordStorageHandler(int portNumber) throws IOException{
		this.m_serverSocket = new ServerSocket(portNumber);
	}

	public void run() {

		Socket socket = null;
		InputStreamReader reader = null;
		BufferedReader in = null;
		System.out.println("Waiting for socket connection...");
		try {
			socket = m_serverSocket.accept();
			reader = new InputStreamReader(socket.getInputStream());
			in = new BufferedReader(reader);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Got Connection.");		
		while(true){			
			try {
				String line = in.readLine();

				System.out.print("RECEIVED " + " ");
				ReverseIndex.storeReverseMapping(line);
				System.out.println("Stored");

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
