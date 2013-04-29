/**
 * DiffMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Computes the currents iteration difference between the previous weight and current weight.
 * Collects all the difference between the rank and previous rank.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class DiffMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> 
{
	public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException 
	{
		//Collect all the differences between the rank and previous rank
		String[] pieces = value.toString().split("\t");
		String[] vals = pieces[1].toString().split("  ");
		double rank = new Double(vals[0]);
		double previousRank = new Double(vals[1]);
		
		double diff = Math.abs(rank-previousRank);
		PageRankDriver.log(new Double(diff).toString());
		context.write(new Text(""), new DoubleWritable(diff));
	}
}