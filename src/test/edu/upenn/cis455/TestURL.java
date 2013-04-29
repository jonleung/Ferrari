package test.edu.upenn.cis455;

import java.net.MalformedURLException;
import java.net.URL;

public class TestURL {

	
	public static void main(String...args) throws MalformedURLException {
		
		URL url = new URL("http://www.google.com/robots.txt");
		System.out.println("host : " + url.getHost());
		
	}
}
