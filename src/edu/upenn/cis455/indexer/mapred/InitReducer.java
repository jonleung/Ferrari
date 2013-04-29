/**
 * InitReducer.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Converts initial input format to intermediate format.
 * Outputs the current page with an initial weight and all its links.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class InitReducer extends Reducer<Text, Text, Text, Text> 
{	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		//if the node does not have any outgoing edges, it will only have "extra" values
		//so no nodes will be added to it's outgoing edges string, but it will still end up with an initial weight

		String nodes = "";
		for(Text v:values)
		{
			String s = v.toString();
			//if the node has outgoing edges, add the outgoing edge to the string
			if(!s.equals("extra"))
			{
				nodes = nodes.concat(s + " ");
			}
		}
		nodes = nodes.trim();

		if (!nodes.equals(""))
		{
			String val = "1.0  1.0  " + nodes;
			PageRankDriver.log(new String(key.toString()) + "\t" + val);
			context.write(new Text(key), new Text(val));
		}
	}
}
