/**
 * OneTimeDriver.java
 * @author Deepthi Shashidhar
 * Computes one iteration of PageRank for a link structure.
 */

package edu.upenn.cis455.pagerank.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.upenn.cis455.pagerank.mapred.FinishMapper;
import edu.upenn.cis455.pagerank.mapred.FinishReducer;
import edu.upenn.cis455.pagerank.mapred.IterMapper;
import edu.upenn.cis455.pagerank.mapred.IterReducer;


public class IterAndFinishDriver 
{

	public static void main(String[] args) throws Exception 
	{
		String iterInput = args[0];
		String iterOutput = args[1];
		String finishInput = args[2];
		String finishOutput = args[3];
		int numReducers = new Integer(args[4]);
		if (!run(iterInput, iterOutput, finishInput, finishOutput, numReducers))
			System.exit(1);
		System.out.println("It all worked.");
		System.exit(0);
	}

	public static boolean run(String iterInput, String iterOutput, String finishInput, String finishOutput, int numReducers) throws Exception
	{

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		System.out.println("iterInput: " + iterInput);
		System.out.println("iterOutput: " + iterOutput);
		System.out.println("output: " + finishOutput);

		Job iterJob = new Job(conf, "iterJob");
		iterJob.setJarByClass(PageRankDriver.class);

		FileInputFormat.addInputPath(iterJob, new Path(iterInput));
		fs.delete(new Path(iterOutput), true);
		FileOutputFormat.setOutputPath(iterJob, new Path(iterOutput));
		iterJob.setMapperClass(IterMapper.class);
		iterJob.setReducerClass(IterReducer.class);
		iterJob.setOutputKeyClass(Text.class);
		iterJob.setOutputValueClass(Text.class);
		iterJob.setNumReduceTasks(numReducers);
		if (!iterJob.waitForCompletion(true))
		{
			System.out.println("Iter Failed.");
			return false;
		}

		Job finishJob = new Job(conf, "finishJob");
		finishJob.setJarByClass(PageRankDriver.class);
		fs.delete(new Path(finishOutput), true);
		FileInputFormat.addInputPath(finishJob, new Path(finishInput));
		FileOutputFormat.setOutputPath(finishJob, new Path(finishOutput));
		finishJob.setMapperClass(FinishMapper.class);
		finishJob.setReducerClass(FinishReducer.class);
		finishJob.setMapOutputKeyClass(DoubleWritable.class);
		finishJob.setMapOutputValueClass(Text.class);
		finishJob.setOutputKeyClass(Text.class);
		finishJob.setOutputValueClass(DoubleWritable.class);
		finishJob.setNumReduceTasks(numReducers);
		if (!finishJob.waitForCompletion(true))
		{
			System.out.println("Finish Failed.");
			return false;
		}

		return true;
	}
}
