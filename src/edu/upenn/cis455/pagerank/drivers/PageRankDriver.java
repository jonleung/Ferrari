/**
 * PageRankDriver.java
 * @author Deepthi Shashidhar
 * Computes the PageRank for a link structure.
 * Chains multiple map reduce job until convergence is reached.
 */

package edu.upenn.cis455.pagerank.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.upenn.cis455.pagerank.mapred.DiffMapper;
import edu.upenn.cis455.pagerank.mapred.DiffReducer;
import edu.upenn.cis455.pagerank.mapred.FinishMapper;
import edu.upenn.cis455.pagerank.mapred.FinishReducer;
import edu.upenn.cis455.pagerank.mapred.InitMapper;
import edu.upenn.cis455.pagerank.mapred.InitReducer;
import edu.upenn.cis455.pagerank.mapred.IterMapper;
import edu.upenn.cis455.pagerank.mapred.IterReducer;


public class PageRankDriver 
{
	
	public static final double C = .85;
	private static final boolean LOG = true;
	public static final int NUM_ITERATIONS = 15;
	
	public static void log(String s)
	{
		if (LOG)
			System.out.println(s);
	}
		
	public static void main(String[] args) throws Exception 
	{
		String input = args[0];
		String output = args[1];
		int numReducers = new Integer(args[2]);
		if (!run(input, output, numReducers))
			System.exit(1);
		System.out.println("It all worked.");
		System.exit(0);
	}
	
	public static boolean run(String input, String output, int numReducers) throws Exception
	{

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		Job initJob = new Job(conf, "initJob");
		initJob.setJarByClass(PageRankDriver.class);
		fs.delete(new Path("initOut"), true);
		FileInputFormat.addInputPath(initJob, new Path(input));
		FileOutputFormat.setOutputPath(initJob, new Path("initOut"));
		initJob.setMapperClass(InitMapper.class);
		initJob.setReducerClass(InitReducer.class);
		initJob.setOutputKeyClass(Text.class);
		initJob.setOutputValueClass(Text.class);
		initJob.setNumReduceTasks(numReducers);
		if (!initJob.waitForCompletion(true))
		{
			System.out.println("Init Failed.");
			return false;
		}

		double currentDiff = Double.MAX_VALUE;
		String currentInputDir = "initOut";
		String currentOutputDir = "";
		int current = 1;

		while (currentDiff > .001)
		{
			System.out.println("On iteration number " + current);
			Job iterJob = new Job(conf, "iterJob");
			iterJob.setJarByClass(PageRankDriver.class);

			currentOutputDir = "iterOut" + current;

			FileInputFormat.addInputPath(iterJob, new Path(currentInputDir));
			fs.delete(new Path(currentOutputDir), true);
			FileOutputFormat.setOutputPath(iterJob, new Path(currentOutputDir));
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

			Job diffJob = new Job(conf, "diffJob");
			diffJob.setJarByClass(PageRankDriver.class);
			fs.delete(new Path("diffOut"), true);
			FileInputFormat.addInputPath(diffJob, new Path(currentOutputDir));
			FileOutputFormat.setOutputPath(diffJob, new Path("diffOut"));
			diffJob.setMapperClass(DiffMapper.class);
			diffJob.setReducerClass(DiffReducer.class);
			diffJob.setMapOutputKeyClass(Text.class);
			diffJob.setMapOutputValueClass(DoubleWritable.class);
			diffJob.setOutputKeyClass(DoubleWritable.class);
			diffJob.setOutputValueClass(Text.class);
			if (!diffJob.waitForCompletion(true))
			{
				System.out.println("Diff Failed.");
				return false;
			}

			Path diffJobPath = new Path("diffOut/part-r-00000");
			FSDataInputStream stream = fs.open(diffJobPath);
			byte[] buffer = new byte[1024];
			int bytesRead;
			String str = "";
			while ((bytesRead = stream.read(buffer)) > 0) 
			{
				log("BytesRead: " + bytesRead);
				str = str + new String(buffer);
			}

			log("CurrentDiffAsString: " + str);
			currentDiff = Double.parseDouble(str);
			log("CurrentDiff: " + currentDiff);

			fs.delete(new Path(currentInputDir), true);
			currentInputDir = currentOutputDir;
			current++;
		}

		Job finishJob = new Job(conf, "finishJob");
		finishJob.setJarByClass(PageRankDriver.class);
		fs.delete(new Path(output), true);
		FileInputFormat.addInputPath(finishJob, new Path(currentOutputDir));
		FileOutputFormat.setOutputPath(finishJob, new Path(output));
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

		fs.delete(new Path(currentInputDir), true);
		fs.delete(new Path(currentOutputDir), true);
		fs.delete(new Path("diffOut"), true);

		return true;
	}
}
