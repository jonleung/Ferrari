/**
 * FinishMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Combines the weights, reformats, and outputs the pages in sorted rank order.
 * Sorts the pages and outputs them with the weight.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class FinishReducer extends Reducer<DoubleWritable, Text, Text, DoubleWritable> 
{	
	public void reduce(DoubleWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		Double rank = new Double(-1*key.get());

		for(Text v: values)
		{
			Text node = new Text(v);
			PageRankDriver.log(node + "\t" + rank);
			context.write(new Text(node), new DoubleWritable(rank));
		}
	}
}