package edu.upenn.cis455.bigfs;

import java.io.IOException;

public class S3Sync {

	public static void main(String[] args) throws IOException, InterruptedException {
//		String[] cmds = {"/bin/sh", "-c", "touch ./bigfs/deepthi.org"};
		String[] cmds = {"/bin/sh", "-c", "s3cmd put -r ./bigfs s3://jonathansbucket/"};

		Process process = Runtime.getRuntime().exec(cmds);
		int num = process.waitFor();
		System.out.println(num);
	}
	
}
