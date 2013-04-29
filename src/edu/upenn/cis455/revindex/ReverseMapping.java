package edu.upenn.cis455.revindex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("serial")
public class ReverseMapping implements Serializable {

	static final String DOC_DELIM = "<_Doc>";
	public String word;
	public HashMap<String, Hit> hits;
	
	public ReverseMapping() {
		this.hits = new HashMap<String, Hit>();
	}
			
	@Override
	public String toString() {
		return String.format("%s: %s", this.word, this.hits.toString());
	}

	public static ReverseMapping parseReverseMappingFromLine(String line) throws Exception {
		String[] lineComponents = line.split(DOC_DELIM);

		ReverseMapping reverseMapping = new ReverseMapping();
		reverseMapping.word = lineComponents[0].trim();
		System.out.println("line components size: " + lineComponents.length);
		for (int i=1; i<lineComponents.length; i++) {
			System.out.println("component: " + lineComponents[i]);
			Hit hit = Hit.parseHit(lineComponents[i]);
			reverseMapping.hits.put(hit.url, hit);
		}
		System.out.println("HIT SIZE: " + reverseMapping.hits.size());
		System.out.println("HIT MAP: " + reverseMapping.hits.toString());
		return reverseMapping;
	}
	
	public String serialize() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(baos);
	    out.writeObject(this);
	    out.flush();
	    out.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
    
    return new String( Base64.encodeBase64(baos.toByteArray()) );
	}
	
	public static ReverseMapping deserialize(String dump) {
    byte[] data = Base64.decodeBase64(dump);
    ObjectInputStream in;
    ReverseMapping reverseMapping = null;
    
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(data));
	    reverseMapping = (ReverseMapping) in.readObject();
	    in.close();
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    
    return reverseMapping;

	}

}