package edu.upenn.cis455.bigfs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PageTest {
	public static void main(String[] args) {
		 
		BufferedReader br = null;
 
		try {
  
			br = new BufferedReader(new FileReader("/Volumes/h/Dropbox/classes/cis_455/eclipse_workspace/TheMiniGoogle/bigfs/smallfile.txt"));
 
			for (int i=0; i<4; i++) {
				String fakeLine = br.readLine();
				System.out.println(Page.parseDocLine(fakeLine).html);				
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
 
	}
}
