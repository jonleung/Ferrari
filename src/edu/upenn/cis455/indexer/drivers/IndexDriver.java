/**
 * Index.java
 * @author Deepthi Shashidhar
 */

package edu.upenn.cis455.indexer.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.upenn.cis455.indexer.mapred.*;

public class IndexDriver 
{
	
	private static final boolean LOG = true;
	
	public static void log(String s)
	{
		if (LOG)
			System.out.println(s);
	}
		
	public static void main(String[] args) throws Exception 
	{
		System.out.println("I am inside the IndexDriver main method....");
		String forwardIndexInput = args[0];
		String forwardIndexOuput = args[1];
		String reverseIndexInput = args[2];
		String reverseIndexOutput = args[3];
		int numReducers = new Integer(args[4]);
		if (!run(forwardIndexInput, forwardIndexOuput, reverseIndexInput, reverseIndexOutput, numReducers))
			System.exit(1);
		log("It all worked.");
		System.exit(0);
	}
	
	private static boolean run(String forwardIndexInput, String forwardIndexOuput, String reverseIndexInput, String reverseIndexOutput, int numReducers) throws Exception
	{
		log("I am inside the IndexDriver run method....");
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		Job forwardJob = new Job(conf, "forwardJob");
		forwardJob.setJarByClass(IndexDriver.class);
		fs.delete(new Path("initOut"), true);
		FileInputFormat.addInputPath(forwardJob, new Path(forwardIndexInput));
		FileOutputFormat.setOutputPath(forwardJob, new Path(forwardIndexOuput));
		forwardJob.setMapperClass(ForwardIndexMapper.class);
		forwardJob.setReducerClass(ForwardIndexReducer.class);
		forwardJob.setOutputKeyClass(Text.class);
		forwardJob.setOutputValueClass(Text.class);
		forwardJob.setNumReduceTasks(numReducers);
		if (!forwardJob.waitForCompletion(true))
		{
			log("Forward Index Failed.");
			return false;
		}

		Job reverseJob = new Job(conf, "reverseJob");
		reverseJob.setJarByClass(IndexDriver.class);
		fs.delete(new Path(reverseIndexOutput), true);
		FileInputFormat.addInputPath(reverseJob, new Path(reverseIndexInput));
		FileOutputFormat.setOutputPath(reverseJob, new Path(reverseIndexOutput));
		reverseJob.setMapperClass(ReverseIndexMapper.class);
		reverseJob.setReducerClass(ReverseIndexReducer.class);
		reverseJob.setOutputKeyClass(Text.class);
		reverseJob.setOutputValueClass(Text.class);
		reverseJob.setNumReduceTasks(numReducers);
		if (!reverseJob.waitForCompletion(true))
		{
			log("Reverse Index Failed.");
			return false;
		}
		return true;
	}
}