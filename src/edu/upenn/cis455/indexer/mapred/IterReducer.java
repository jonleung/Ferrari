/**
 * IterReducer.java
 * @author Deepthi Shashidhar
 * MapReduce Job: Does one PageRank iteration, pushing weight onto the next nodes.
 * Outputs the new weight computed from combining pushed weights and by refactoring.
 */

package edu.upenn.cis455.indexer.mapred;

import java.io.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import edu.upenn.cis455.indexer.drivers.PageRankDriver;

public class IterReducer extends Reducer<Text, Text, Text, Text> 
{	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		//add the weights of one node together and multiplies them by the appropriate values
		//stores the previous rank as well as the new rank
		double weightCalc = 0;
		double rank = 0;
		double previousRank = 0;
		String nodes = "";
		
		for(Text v: values)
		{
			String s = v.toString();
			if(s.startsWith("Weight"))
			{
				weightCalc += new Double(s.split(" ")[1]);
			}
			else
			{
				String[] vals = s.split("  ");
				rank = new Double(vals[0]);
				previousRank = new Double(vals[1]);
				if(vals.length == 3)
					nodes = vals[2];
			}
		}
		previousRank = rank;
		rank = weightCalc*PageRankDriver.C + (1-PageRankDriver.C);	
		
		String val = rank + "  " + previousRank + "  " + nodes;
		PageRankDriver.log(new String(key.toString()) + "\t" + val);
		context.write(new Text(key), new Text(val));
	}
}
