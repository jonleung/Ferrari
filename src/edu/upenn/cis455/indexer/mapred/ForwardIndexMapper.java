package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.upenn.cis455.indexer.Helpers;
import edu.upenn.cis455.indexer.Page;
import edu.upenn.cis455.indexer.Stemmer;
import edu.upenn.cis455.indexer.drivers.IndexDriver;


public class ForwardIndexMapper extends Mapper<LongWritable, Text, Text, Text>
{
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		IndexDriver.log("Inside ForwardIndexMapper");
		String val = new String(value.toString());
		Page params = Page.parseDocLine(val);

		Document doc = Jsoup.parse(params.getHtml(), params.getUrl());
		String text = doc.text().trim();
		
		String[] words = text.split(" ");
		
		for (int pos = 0; pos < words.length; pos++)
		{
			String word = words[pos].trim();
			word = word.replaceAll("[^A-Za-z]", "");

			if (Helpers.isStopWord(word) || word.equals(""))
			{
				continue;
			}
			
			//TODO: get relative font size
			String relativeFontSize = "10";
			String position = Integer.toString(pos);
			//TODO: find out if it really is fancy or not
			String isFancy = "false";

			Stemmer stemmer = new Stemmer();
			stemmer.add(word.toLowerCase().toCharArray(), word.length());
			stemmer.stem();
			String stem = stemmer.toString();
			if (stem.equals(""))
			{
				continue;
			}
			String outputVal = stem + "  " + word + "  " + relativeFontSize + "  " + position + "  " + isFancy;
			
			IndexDriver.log(params.getUrl() + "\t" + outputVal);
			context.write(new Text(params.getUrl()), new Text(outputVal));
		}
	}
}
