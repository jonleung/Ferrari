/**
 * IterMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Does one PageRank iteration, pushing weight onto the next nodes.
 * Outputs the weights to be pushed onto the links from the given page.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class IterMapper extends Mapper<LongWritable, Text, Text, Text> 
{
	public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException 
	{
		//pushes the right weights onto the next nodes
		String[] pieces = value.toString().split("\t");
		String realKey = pieces[0];
		String[] vals = pieces[1].toString().split("  ");
		double rank = new Double(vals[0]);
		if(vals.length == 3)
		{
			String[] nodes = vals[2].split(" ");
			double weight = (double)(rank/(double)nodes.length);
			for(String n:nodes)
			{
				PageRankDriver.log(n + "\t" + weight);
				context.write(new Text(n), new Text("Weight " + weight));
			}
		}
		PageRankDriver.log(realKey + "\t" + pieces[1] + "\n");
		context.write(new Text(realKey), new Text(pieces[1]));
	}
}
