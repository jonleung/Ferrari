package edu.upenn.cis455.indexer.mapred;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.upenn.cis455.indexer.drivers.IndexDriver;

public class ReverseIndexMapper extends Mapper<LongWritable, Text, Text, Text>
{
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		IndexDriver.log("Inside ReverseIndexerMapper");
		String[] pieces = new String(value.toString()).split("\t");
		String url = pieces[0];
		String stem = pieces[1];
		String termFrequency = pieces[2];
		String score = pieces[3];
		String positions = pieces[4];
		String outputVal = url + "\t" + termFrequency + "\t" + score + "\t" + positions;
		IndexDriver.log(stem + "\t" + outputVal);
		context.write(new Text(stem), new Text(outputVal));
		
	}
}
