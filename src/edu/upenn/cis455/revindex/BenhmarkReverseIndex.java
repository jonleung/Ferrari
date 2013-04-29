package edu.upenn.cis455.revindex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

import edu.upenn.cis455.analytics.Analytics;

public class BenhmarkReverseIndex {
	
	final static int NUM_VALUES = 20;
	final static int NUM_DOCS = 1000;
	final static int INTERVAL = 1000;
	final static int NUM_ITERATIONS = 1000;
	
	public static void benchmarkArraySplitting() {
		StringBuffer stringBuffer = new StringBuffer();
		
		int curNum = 0;
		for (int i=0; i<NUM_VALUES; i++) {
			stringBuffer.append(String.format("%d,", curNum));
			curNum += INTERVAL;
		}
		
		String string = stringBuffer.toString();
		
		Analytics.logStartTime("benchmarkArraySerialization");
		for (int docCount=0; docCount<NUM_DOCS; docCount++) {
			for (int it=0; it<NUM_ITERATIONS; it++) {
				String[] stringPositions = string.split(",");
				int[] positions = new int[stringPositions.length];
				for (int i=0; i<stringPositions.length; i++) {
					positions[i] = Integer.parseInt(stringPositions[i]);
				}
			}
		}
		Analytics.logEndTime("benchmarkArraySerialization");
	}
	
	// http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
	public static void benchmarkSimpleSerializationOfArray() throws FileNotFoundException, IOException, ClassNotFoundException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
    int[] positions = new int[NUM_VALUES];
    for (int i=0; i<NUM_VALUES; i++) {
    	positions[i] = i;
    }
    out.writeObject(positions);
    out.flush();
    out.close();
    
    String encodedArray = new String( Base64.encodeBase64(baos.toByteArray()) );
   
    Analytics.logStartTime("benchmarkJavaSerialization");
    for (int docCount=0; docCount<NUM_DOCS; docCount++) {
      for (int i=0; i<NUM_ITERATIONS; i++) {
        byte[] data = Base64.decodeBase64(encodedArray);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
        int[] array = (int[]) in.readObject();
        in.close();
      }
    }
    Analytics.logEndTime("benchmarkJavaSerialization");
	}
	public static void benchmarkSerializationOfHashMap() throws FileNotFoundException, IOException, ClassNotFoundException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		
    HashMap<String, ArrayList<Integer>> mapping = new HashMap<String, ArrayList<Integer>>();
    for (int docCount=0; docCount<NUM_DOCS; docCount++) {
      for (int i=0; i<NUM_VALUES; i++) {
      	ArrayList<Integer> positions = new ArrayList<Integer>();
      	mapping.put("http://google.com/asdf/gearf/q34raf/e/q34fawe/q34gfwae", positions);
      }
    }
    out.writeObject(mapping);
    out.flush();
    out.close();
    
    String encodedArray = new String( Base64.encodeBase64(baos.toByteArray()) );
   
    Analytics.logStartTime("benchmarkJavaSerializationOfHashMap");
    for (int i=0; i<NUM_ITERATIONS; i++) {
      byte[] data = Base64.decodeBase64(encodedArray);
      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
      HashMap<String, ArrayList<Integer>> deserializedMapping = (HashMap<String, ArrayList<Integer>>) in.readObject();
      in.close();
    }
    Analytics.logEndTime("benchmarkJavaSerializationOfHashMap");
	}
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
//		benchmarkArraySplitting();		
//		benchmarkSimpleSerializationOfArray();
		benchmarkSerializationOfHashMap();
	}
}
