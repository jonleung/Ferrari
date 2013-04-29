package edu.upenn.cis455.analytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analytics {

	
	static Map<String, Date> typeTimeMapping = new HashMap<String, Date>();
		
	public static void logStartTime(String string) {
		Date leftoverDate = typeTimeMapping.get(string);
		if (leftoverDate != null) {
			throw new RuntimeException("Date should be reset");
		}
		Date now = new Date();
		typeTimeMapping.put(string, now);
	}
	
	public static void logEndTime(String string) {
		double endTime = (new Date()).getTime();
		double startTime = typeTimeMapping.get(string).getTime();
		double oneThousand = 1000;
		double secondsDiff = (endTime - startTime)/oneThousand;
		String line = String.format("%s took %f to run", string, secondsDiff);
		writeToLog(line);
		typeTimeMapping.put(string, null);
	}
	
	public static void logError(Exception e) {
		e.printStackTrace();
	}
	
	public static void logCrawledUrl(String url) {
		writeToLog(String.format("Crawled %s", url));
	}
	
	public static void logNonstandardUrl(String type, String url) {
		writeToLog(String.format("%s %s", type, url));
	}
	
	private static void writeToLog(String line) {
		System.out.println(line);
	}

	
	
	
}
