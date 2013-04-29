package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

// This class gets the document URL from the user, extracts the domain 
// and sends a request to the server. This is basically an http client.
public class DocumentFetcher {

	public class MetaData {
		public String pageContent;
		public int contentLength = -1;
		public boolean isxml;
	}


	public DocumentFetcher() {
		// Do nothing.
	}

	public String getRobotsTxt(String url) throws IOException {

		//		System.out.println("ROBOT URL: " + url);
		// get the file name from the URL
		String uri = null;
		// get the domain name out.
		if (url.contains("://")) {
			String[] parts = url.split("://");
			uri = parts[1]; // this will contain everythng after http://
		} else {
			uri = url;
		}
		String domain = uri.substring(0, uri.indexOf('/'));
		String resource = uri.substring(uri.indexOf('/'));
		//		System.out.println("Domain: " + domain + " Resource: " + resource);

		// get the streams to read and write to the server.
		PrintWriter out = null;
		BufferedReader in = null;
		Socket socket = null;
		try {
			socket = new Socket(domain, 80);
			socket.setSoTimeout(SOCKET_TIMEOUT);  // socket timeout.
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.out.println("Unknown host:" + domain);
			return null;
		} catch (IOException e) {
			System.out.println("No I/O");
			return null;
		}

		// System.out.println("Connection Successful");

		// now its time to send the request.
		out.println(makeHeader(domain, resource));
		out.flush();
		//		System.out.println("SENT REQUEST FOR ROBOT " + makeHeader(domain, resource));
		// the response will come now.
		ArrayList<String> response = new ArrayList<String>();
		String strLine = in.readLine();
		//		System.out.println("first line:" + strLine );
		while (strLine != null && strLine.length() != 0) {
			//			System.out.println(strLine);
			response.add(strLine.replaceAll("[\\n\\t ]", " ").replaceAll(
					"( )+", " "));
			strLine = in.readLine();
		}
		//		System.out.println("getRobotsTxt: END of HEAD read");

		// read the first line and check if there is any error.
		if (response == null || !response.get(0).split(" ")[1].equals("200")) {
			// there is some error.
			System.out.println("Some error");
			return null;
		}

		int contentLength = 0;
		boolean chunkedEncoding = false;
		for (String header : response) {
			if (header.toLowerCase().contains("content-length")) {
				contentLength = Integer.parseInt(header.split(":")[1].trim());
			}
			if(header.toLowerCase().contains("transfer-encoding")){
				chunkedEncoding = header.split(":")[1].trim().equals("chunked");
			}
			// else System.out.println("Doc Fetcher: no content length !");
		}

		//		System.out.println("ROBOT CONTENT LENGTH: " + contentLength);
		StringBuilder robotTxt = new StringBuilder();
		if (contentLength != 0) {
			// now read as many bytes as the content length says it has.
			for (int i = 0; i < contentLength; i++) {
				robotTxt.append((char)in.read());
			}
		}
		else if(chunkedEncoding == true){
						int chunkLength = 0;
						strLine = in.readLine();
						System.out.println(strLine);
						if(strLine != null && strLine.contains(";")) {
							strLine = strLine.split(";")[0].trim();				
						}
						chunkLength = Integer.parseInt(strLine, 16);
//						System.out.println("Chunk length " + chunkLength);
						while(chunkLength != 0){
							int i;
							for (i = 0; i < chunkLength; i++) {
								robotTxt.append((char)in.read());
								
							}
			//				System.out.println(content);
							// ignore the CLRF.
							in.read();
							in.read();
							System.out.println("after reading one chunk " + i);
							strLine = in.readLine();
							if(strLine != null && strLine.contains(";")) {
								strLine = strLine.split(";")[0].trim();				
							}
							chunkLength = Integer.parseInt(strLine, 16);
//							System.out.println("Chunk " + chunkLength);
						}
	
		}
		else{
			// it is not chunked encoding and doesn't give
			// content length.
			// TODO: check this return.
			return null; 
		}

		//		System.out.println("ROBOTS:-");
		//		System.out.println(robotTxt.toString());
		out.close();
		in.close();
		socket.close();

		// System.out.println("Written to file " + docFile.getAbsolutePath());
		return robotTxt.toString();
	}

//	public MetaData getDocument(String url, InetAddress address) throws IOException {
//
//		MetaData retVal = new MetaData();
//
//		String uri = null;
//		// get the domain name out.
//		if (url.contains("://")) {
//			String[] parts = url.split("://");
//			uri = parts[1]; // this will contain everything after http://
//		} else {
//			uri = url;
//		}
//		String domain = uri; // by default.
//		String resource = new String("/");
//		if (uri.contains("/")) {
//			domain = uri.substring(0, uri.indexOf('/'));
//			resource = uri.substring(uri.indexOf('/'));
//		}
//
//		// System.out.println("Domain: " + domain + " Resource: " + resource);
//
//		// get the streams to read and write to the server.
//		PrintWriter out = null;
//		BufferedReader in = null;
//		Socket socket = null;
//		try {
//			socket = new Socket(domain, 80);
//			socket.setSoTimeout(SOCKET_TIMEOUT);  // socket timeout.
//			out = new PrintWriter(socket.getOutputStream(), true);
//			in = new BufferedReader(new InputStreamReader(
//					socket.getInputStream()));
//		} catch (UnknownHostException e) {
//			//			System.out.println("Unknown host:" + domain);
//			return null;
//		} catch (IOException e) {
//			//			System.out.println("No I/O");
//			return null;
//		}
//
//		System.out.println("Connection Successful");
//
//		// now its time to send the request.
//		out.println(makeHeader(domain, resource));
//		out.flush();
//
//		// the response will come now.
//		ArrayList<String> response = new ArrayList<String>();
//		String strLine = in.readLine();
//		System.out.println("first line:" + strLine );
//		while (strLine != null && strLine.length() != 0) {
//			System.out.println(strLine);
//			response.add(strLine.replaceAll("[\\n\\t ]", " ").replaceAll(
//					"( )+", " "));
//			strLine = in.readLine();
//		}
//
//		// read the first line and check if there is any error.
//		if (response == null
//				|| (!response.get(0).split(" ")[1].equals("200") && !response
//						.get(0).split(" ")[1].equals("301"))) {
//			// there is some error.
//			//			System.out.println("response " + response + " returning");
//			return null;
//		}
//
//		int contentLength = 0;
//		boolean chunkedEncoding = false;
//		for (String header : response) {
//			System.out.println("HEADER: " + header);
//			if (header.toLowerCase().contains("content-length")) {
//				contentLength = Integer.parseInt(header.split(":")[1].trim());
//				retVal.contentLength = contentLength;
//			}
//			if(header.toLowerCase().contains("transfer-encoding")){
//				System.out.println("CHUNK TRUE");
//				chunkedEncoding = header.toLowerCase().contains("chunked");
//			} 				
//			if (header.toLowerCase().startsWith("content-type")) {
//				if (header.contains("text/xml")
//						|| header.contains("application/xml")
//						|| header.endsWith("+xml")) {
//					retVal.isxml = true;
//				} else if (header.contains("text/html") || header.contains("text/plain")) {
//					retVal.isxml = false;
//				}
//				else 
//				{
//					// we are not processing this document.
//					System.out.println("NOT HTML OR XML doc");
//					return null;
//				}
//			}
//			// else System.out.println("Doc Fetcher: no content length !");
//		}
//
//		StringBuilder content = new StringBuilder();
//		if (contentLength != 0) {
//			// now read as many bytes as the content length says it has.
//			for (int i = 0; i < contentLength; i++) {
//				content.append((char)in.read());
//			}
//		}
//		else if(chunkedEncoding){
//			//			int chunkLength = 0;
//			//			strLine = in.readLine();
//			//			System.out.println(strLine);
//			//			if(strLine != null && strLine.contains(";")) {
//			//				strLine = strLine.split(";")[0].trim();				
//			//			}
//			//			chunkLength = Integer.parseInt(strLine, 16);
//			//			System.out.println("Chunk length " + chunkLength);
//			//			while(chunkLength != 0){
//			//				int i;
//			//				for (i = 0; i < chunkLength; i++) {
//			//					content.append((char)in.read());
//			//					
//			//				}
//			////				System.out.println(content);
//			//				// ignore the CLRF.
//			//				in.read();
//			//				in.read();
//			//				System.out.println("after reading one chunk " + i);
//			//				strLine = in.readLine();
//			//				if(strLine != null && strLine.contains(";")) {
//			//					strLine = strLine.split(";")[0].trim();				
//			//				}
//			//				chunkLength = Integer.parseInt(strLine, 16);
//			//				System.out.println("Chunk " + chunkLength);
//			//			}
//			boolean readAll = false;
//			System.out.println("starting to read the document");
//			int length =0;
//			while(!readAll) {
//				char reading = (char)in.read();
//				System.out.print(reading);
//				content.append(reading);
//				length = content.length();
//				if(length > 5) {
//					if(content.substring(length-5, length).equals("\r\n0\r\n")) {
//						readAll = true;
//					}
//				}
//			}	
//			System.out.println("ended reading the document");
//			retVal.pageContent = content.toString();
//			retVal.pageContent = retVal.pageContent.replaceAll("\r\n[a-f0-9]+\r\n", "");
//		}
//		else{
//			// it is not chunked encoding and doesn't give
//			// content length.
//			// TODO: check this return.
//			System.out.println("Unsupported encoding");
//			return null; 
//		}
//		out.close();
//		in.close();
//		socket.close();
//
//		return retVal;
//	}

	public HashMap<String, String> doHEAD(String url) throws IOException {
		HashMap<String, String> headerMap = new HashMap<String, String>();

		// get the file name from the URL;

		String URI = null;
		// get the domain name out.
		if (url.contains("://")) {
			String[] parts = url.split("://");
			URI = parts[1]; // this will contain everythng after http://
		} else {
			URI = url;
		}
		String domain = URI; // by default.
		String resource = new String("/");
		if (URI.contains("/")) {
			domain = URI.substring(0, URI.indexOf('/'));
			resource = URI.substring(URI.indexOf('/'));
		}

		// get the streams to read and write to the server.
		PrintWriter out = null;
		BufferedReader in = null;
		Socket socket = null;
		try {
			//			System.out.println("ADDRESS " + address.toString());
			//			socket = new Socket(address, 80);
			socket = new Socket(domain, 80);
			socket.setSoTimeout(SOCKET_TIMEOUT);  // socket timeout.
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			//			System.out.println("Unknown host:" + domain);
			return null;
		} catch (IOException e) {
			//			System.out.println("No I/O");
			return null;
		}

		//		System.out.println("Connection Successful");

		// now its time to send the request.
		out.println(makeHeader(domain, resource));
		out.flush();

		// the response will come now.
		ArrayList<String> response = new ArrayList<String>();
		String strLine = in.readLine();
		//		System.out.println("first line:" + strLine);
		while (strLine != null && strLine.length() != 0) {
			//			System.out.println(strLine);
			response.add(strLine.replaceAll("[\\n\\t ]", " ").replaceAll(
					"( )+", " "));
			strLine = in.readLine();
		}

		// read the first line and check if there is any error.
		if (response == null
				|| (!response.get(0).split(" ")[1].equals("200") && !response
						.get(0).split(" ")[1].equals("301"))) {
			// there is some error.
			//			System.out.println("There is some error RESOPONSE " + response.toString());
			return null;
		}

		StringBuilder headers = new StringBuilder();

		// put default content type.
		headerMap.put("content-length", "-1");
		for (String header : response) {
			headers.append(header);
			if (header.toLowerCase().contains("content-length")) {
				headerMap.put("content-length", header.split(":")[1].trim());
			}
			if (header.toLowerCase().startsWith("content-type")) {
				//				System.out.println("HEADER: " + header);
				if (header.toLowerCase().contains("text/xml")
						|| header.toLowerCase().contains("application/xml")
						|| header.toLowerCase().endsWith("+xml")) {
					headerMap.put("content-type", "xml");
				} else if (header.toLowerCase().contains("text/html")) {
					headerMap.put("content-type", "html");
				} else {
					//					System.out.println("RETURNING FROM HERE");
					// we are not processing this document.
					return null;
				}
			}
			if (header.toLowerCase().contains("last-modified")) {
				headerMap.put("last-modified",
						header.substring(header.indexOf(':') + 1));
			}
		}
		headerMap.put("headersString", headers.toString());
		out.close();
		in.close();
		socket.close();
		//		System.out.println("returning from doHEAD");
		return headerMap;

	}

	private String makeHeader(String domain, String resource) {
		String header = new String("GET " + resource + " HTTP/1.1\n" + "Host:"
				+ domain + "\n" + "User-Agent: cis455Crawler\n" +
						"Accept-Language: en-US\n\n");
		// System.out.println("sent request: " + header);
		return header;
	}

	final int SOCKET_TIMEOUT = 5000;
}
