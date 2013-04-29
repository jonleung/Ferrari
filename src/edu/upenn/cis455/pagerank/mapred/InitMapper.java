/**
 * InitMapper.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Converts initial input format to intermediate format.
 * Outputs the nodes to be combined in the reducer.
 */

package edu.upenn.cis455.pagerank.mapred;

import java.io.IOException;

import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.io.*;

import edu.upenn.cis455.pagerank.drivers.PageRankDriver;

public class InitMapper extends Mapper<LongWritable, Text, Text, Text> 
{
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String s = value.toString();
		String[] vals = s.split(" ");
		for (String v: vals)
		{
			PageRankDriver.log("vals[i]: " + v);
		}

		if (vals.length > 1)
		{
			PageRankDriver.log(vals[0] + "\t" + vals[1]);

			//output the node and the node it is connected to
			context.write(new Text(vals[0]), new Text(vals[1]));

			//if a node isn't pointing to anything, it still needs an initial value - this is used for that reason
			//ie, in the example, node 4 didn't have any outgoing edges, but it still needs to be accounted for
			context.write(new Text(vals[1]), new Text("extra"));
		}
		else if (!vals[0].equals(""))
		{
			context.write(new Text(vals[0]), new Text("extra"));
		}
	}
}