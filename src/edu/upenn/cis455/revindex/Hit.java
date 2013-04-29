package edu.upenn.cis455.revindex;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Hit implements Serializable {
	
	static final String HIT_PROP_DELIM = "<_Prop>";
	static final String POS_DELIM = ",";

	public Hit() {}

	public static Hit parseHit(String line) {
		
		String[] hitComponents = line.split(HIT_PROP_DELIM);
		Hit hit = new Hit();
		
		hit.url = hitComponents[0];		
		hit.wordRelevance = Double.parseDouble(hitComponents[1]);

		//#########################################
		// Positions
		String[] positionsString = hitComponents[2].split(POS_DELIM);
		int[] positions = new int[positionsString.length];
		for (int posIndex=0; posIndex < positionsString.length; posIndex++) {
			positions[posIndex] = Integer.parseInt(positionsString[posIndex]);
		}
		hit.positions = positions;
		
		hit.title = hitComponents[3];
		hit.previewText = hitComponents[4];

		hit.fetchPageRank();
		return hit;
	}

	public void fetchPageRank() {
		try {
			this.pageRank = PageRankIndex.getRank(this.url);
		}
		catch (NullPointerException e) {
			System.out.println(String.format("Unable to find pagerank score for %s", this.url));
			e.printStackTrace();
		}
		
	}

	@Override
	public String toString() {
		return String.format("\n\t<score: %f, url:%s, positions: %s>", this.wordRelevance, this.url, this.positions.toString());
	}
	
	public String url;
	public double wordRelevance;
	public double pageRank;
	public String title;
	public String previewText;
	int[] positions;

}
