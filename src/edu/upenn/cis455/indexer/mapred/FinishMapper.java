/**
 * FinishMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Combines the weights, reformats, and outputs the pages in sorted rank order.
 * Outputs the weight with the link, but with weight as key to be able to sort.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class FinishMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> 
{
	public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException 
	{		
		String[] splits = value.toString().split("\t");
		String realKey = splits[0];
		String realValues = splits[1];
		String[] vals = realValues.split("  ");
		String rank = vals[0];
		PageRankDriver.log(realKey + "\t" + rank);
		context.write(new DoubleWritable(Double.parseDouble(rank)*-1), new Text(realKey));
	}
}

