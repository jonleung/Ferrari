package edu.upenn.cis455.indexer.pagerank;

import java.io.File;
import edu.upenn.cis455.revindex.PageRankIndex;

public class PageRankStorageAdapter {

	public static void main(String...args) {

		if(args.length != 1) {
			System.out.println("Usage: PageRankStorageAdapter <pagerank directory>");
			System.exit(0);
		}
		save(args[0]);
	}

	public static void save(String dirname) {
		File dir = new File(dirname);
		for (File filename : dir.listFiles()) {
			if(filename.isDirectory()) {
				save(filename.getAbsolutePath());
			}
			PageRankIndex.storePageRank(filename.getAbsolutePath());
		}
	}
}