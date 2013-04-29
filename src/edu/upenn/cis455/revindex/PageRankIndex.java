package edu.upenn.cis455.revindex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class PageRankIndex {

	public static Jedis redis = new Jedis("localhost");
	public static Pipeline redisPipeline = redis.pipelined();
	public static String REDIS_NAMESPACE = "pr";
	public static boolean DEBUG = true;

	public static void sync() {
		redisPipeline.sync();
	}
	
	public static String generateKey(String word) {
		return String.format("%s:%s", REDIS_NAMESPACE, word);
	}
	
	public static double getRank(String url) {
		String pageRankString = redis.get(generateKey(url));
		double pageRank = Double.parseDouble(pageRankString);
		return pageRank;
	}
	
	public static void storePageRank(String line) {
		String[] urlPageRankMapping = line.split("\t");
		String url = urlPageRankMapping[0];
		String pageRankString = urlPageRankMapping[1];
		setRank(url, pageRankString);
	}
	
	public static void setRank(String url, String pageRankString) {
		String key = generateKey(url);
		redisPipeline.set(key, pageRankString);
		System.out.println(String.format("Stored: <%s, %s>", url, pageRankString));
	}
	
	public static void reset() {
		Set<String> keys = redis.keys(REDIS_NAMESPACE+"*");
		int numKeys = keys.size();
		for (String key : keys) {
			redisPipeline.del(key);
		}
		redisPipeline.sync();
		System.out.println(String.format("Deleted %d keys", numKeys));
	}
	
	public static void storePageRankFromFile(String path) {
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				storePageRank(line);
			}
			redisPipeline.sync();
			bufferedReader.close();
		}
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		storePageRankFromFile("/Volumes/h/Dropbox/classes/cis_455/eclipse_workspace/TheMiniGoogle/bigfs/example.pagerank");
		reset();
	}

	
	
}
