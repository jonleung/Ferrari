package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

public class RobotParser {
	// this function takes the domain and returns the following information about
	// robots.txt. 1. the crawl delay if present defaulted to 0.
	// 2. the list of disallow URIs.
	public static ArrayList<String> parseRobot(String domain) {

		//		System.out.println("ROBOT PARSER:" + domain);
		ArrayList<String> retVal = new ArrayList<String>();
		// make sure there is a default value of the crawl delay set.
		retVal.add("0");
//		System.out.println("DOMAIN RECEIVED: " + domain);

		String robotTxt = null;
		try {
			robotTxt = Jsoup.connect("http://"+domain+"/robots.txt").timeout(3000).get().text();
		} catch (IOException e) {
			System.out.println(domain + "NOT FOUND");
			e.printStackTrace();
		}

		//		String robotTxt = docFetcher.getRobotsTxt(domain + "/robots.txt");
		if(robotTxt == null){// we did not get the document.
			System.out.println("null ROBOT TXT");
			//			System.out.println("Problem in parsing the document.");
			return retVal;
		}

		//		System.out.println("ROBOT TXT:" + robotTxt);
		String strContentLower = robotTxt.toLowerCase();
		String otherPart = null;

		if(strContentLower.contains("cis455crawler")) {
			otherPart = robotTxt.substring(robotTxt.toLowerCase().indexOf("cis455crawler")+13);
			if(otherPart.toLowerCase().contains("user-agent")){
				otherPart = otherPart.substring(0, otherPart.toLowerCase().indexOf("user-agent"));
			}
		} else if(strContentLower.contains("user-agent: *")) {		
			otherPart = robotTxt.substring(robotTxt.toLowerCase().indexOf("user-agent: *")+13);
			if(otherPart.toLowerCase().contains("user-agent")){
				otherPart = otherPart.substring(0, otherPart.toLowerCase().indexOf("user-agent"));
			}
		} else if(strContentLower.contains("user-agent:*")) {		
			otherPart = robotTxt.substring(robotTxt.toLowerCase().indexOf("user-agent:*")+12);
			if(otherPart.toLowerCase().contains("user-agent")){
				otherPart = otherPart.substring(0, otherPart.toLowerCase().indexOf("user-agent")); 
			} 
		} else {
			return retVal;
		}


		//System.out.println("OTHERPART IS " + otherPart);
		// now the other part contains Disallow and/or crawl-delay or nothing.
		if(otherPart != null ) {
			otherPart = otherPart.replaceAll(":\\s+", ":");
			otherPart = otherPart.replaceAll("\\s+", "\n");
//			System.out.println("other part after regular exp: " + otherPart);
			String[] parts = otherPart.split("\n");
			for(String str:parts) {
				if(str.contains(":")){
					String[] pair = str.split(":");
					if(pair[0].trim().equalsIgnoreCase("Disallow") && pair.length != 1) {
						// get the string in.
						retVal.add(pair[1].trim());
//						System.out.println("Robot Parser: " + str);
					}
					else if(pair[0].trim().equalsIgnoreCase("Crawl-delay")) {
						retVal.set(0, pair[1].trim());
//						System.out.println("Robot Parser: " + str);
					}
				}
			}
		}
//		System.out.println("RETURN VALUES:" + retVal);
		// now the arraylist should contain the required elements.
		return retVal;
	}
}
