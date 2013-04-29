package edu.upenn.cis455.revindex;

import java.util.Comparator;

public class HitScoreComparator implements Comparator<Hit> {
	
	@Override
	public int compare(Hit h1, Hit h2) {
	if(h1.wordRelevance < h2.wordRelevance) return 1;
	else if(h1.wordRelevance > h2.wordRelevance) return -1;
	return 0; 
	}
}