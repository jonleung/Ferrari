package edu.upenn.cis455.bigfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BigFile {
	
	private long LENGTH_THRESHOLD = 64000000;
	
	private static final int VERSION = 0;
	private static final double MB = (Math.pow(1024, 2));

	private String nodeId;
	
	File file;
	BufferedWriter bufferedWriter;
	long length;
	File parentDir;
	String filename;
	String type;
	
	public BigFile(String type, File parentDir, String nodeId) throws IOException {;
		this.type = type;
		if ((!this.type.equals("url") && !this.type.equals("doc"))) {
			throw new RuntimeException(String.format("Invalid Type: %s. Must be of type 'url' or 'doc'", this.type));
		}
		this.parentDir = parentDir;
		this.nodeId = nodeId;
		this.setFilename();
				
		this.file = new File(parentDir.getPath(), this.filename);
		FileWriter fileWriter = new FileWriter(file);
		
		int bufferSize = 4 * (int) MB;
		if (BigFs.DEBUG) bufferSize = 1024;
		bufferedWriter = new BufferedWriter(fileWriter, bufferSize);
	}
		
	private void setFilename() {
	  Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("MMMMdd_hh-mm-ssa");	  		
		
		String currentUnixTime = Long.toString(System.currentTimeMillis());
		String filePrefix = String.format("%s_%s_%s_node%s", this.type, currentUnixTime, dateFormat.format(date), nodeId);
		String fileExtension = String.format("bigfile_v%d", VERSION);
		this.filename = String.format("%s.%s", filePrefix, fileExtension); 
	}
			
	public void append(String string) throws IOException {
		this.length += string.length();
		bufferedWriter.write(string);
		if (BigFs.DEBUG) System.out.println(String.format("Write %d chars to %s's bufferedWriter", string.length(), this.filename));
	}
	
	public void flush() throws IOException {
		bufferedWriter.flush();
		bufferedWriter.close();
		if (BigFs.DEBUG) System.out.println(String.format("Closed file %s", this.filename));
	}
	
	public long length() {
		return this.length;
	}
	
	public double getMegabytes() {
		return this.file.length() / 1024.0 / 1024.0;
	}
	
	public long getLengthThreshold() {
		return LENGTH_THRESHOLD;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public File getFile() {
		return this.file;
	}

	
}
