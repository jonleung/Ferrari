/**
 * DiffMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Computes the currents iteration difference between the previous weight and current weight.
 * Finds the largest difference and outputs it.
 */


package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class DiffReducer extends Reducer<Text, DoubleWritable, DoubleWritable, Text> 
{	
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException 
	{
		//find the largest difference from the input
		double largest = 0;
		for(DoubleWritable v: values)
		{
			double num = v.get();
			if(num > largest)
				largest = num;
		}
		PageRankDriver.log(new Double(largest).toString());
		context.write(new DoubleWritable(largest), new Text(""));
	}
}
