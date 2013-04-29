package edu.upenn.cis455.revindex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ReverseIndex {
	
	public static Jedis redis = new Jedis("localhost");
	public static Pipeline redisPipeline = redis.pipelined();
	public static String REDIS_NAMESPACE = "ri";
	public static boolean DEBUG = true;
	
	public static String generateKey(String word) {
		return String.format("%s:%s", REDIS_NAMESPACE, word);
	}
	
	public static void sync() {
		redisPipeline.sync();
	}
	
	public static void storeReverseMapping(String line) {
		ReverseMapping reverseMapping = null;
		try {
		reverseMapping = ReverseMapping.parseReverseMappingFromLine(line);
		} catch (Exception e) {
			return;
		}
		if (DEBUG) System.out.println(String.format("Stored: %s", reverseMapping.toString()));
		String key = generateKey(reverseMapping.word);
		redis.set(key, reverseMapping.serialize());
	}

	public static void storeReverseMappingFromFile(String path) {
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				storeReverseMapping(line);
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
	
	public static String retrieveSerializedReverseMapping(String word) {
		String key = generateKey(word);
		return redis.get(key);
	}

	public static ReverseMapping retrieveReverseMapping(String word) {
		String serializedReverseMapping = retrieveSerializedReverseMapping(word);
		ReverseMapping reverseMapping = ReverseMapping.deserialize(serializedReverseMapping);
		if (DEBUG) System.out.print(String.format("Retrieved: %s", reverseMapping.toString()));
		return reverseMapping;
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
	
	public static void main(String[] args) {
//		ReverseIndex.reset();
		storeReverseMappingFromFile("/Volumes/h/Dropbox/classes/cis_455/eclipse_workspace/TheMiniGoogle/bigfs/example.reverseindex");
//		retrieveReverseMapping("a");
	}
}

//Old Code that does not use serialization				
//System.out.println(reverseMapping);
//for (Hit hit : reverseMapping.hits) {
//redisPipeline.zadd(reverseMapping.word, hit.score, hit.url);
//redisPipeline.hsetnx(reverseMapping.word, hit.url, hit.positions);
//}

//public static ReverseMapping retrieveReverseMapping(String word) {		
//	ReverseMapping reverseMapping = new ReverseMapping();
//	reverseMapping.word = word;
//	
//	Set<Tuple> tupleSet = redis.zrangeWithScores(word, 0, -1);
//	Iterator<Tuple> it = tupleSet.iterator();
//	while (it.hasNext()) {
//		Hit hit = new Hit();
//		Tuple hitTuple = it.next();
//		hit.url = hitTuple.getElement();
//		hit.score = hitTuple.getScore();			
//		reverseMapping.hits.add(hit);
//	}
//	System.out.println(reverseMapping);
//	return reverseMapping;
//}
//
