/**
 * OneTimeDriver.java
 * @author Deepthi Shashidhar
 * Computes one iteration of PageRank for a link structure.
 */

package edu.upenn.cis455.indexer.drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import edu.upenn.cis455.indexer.mapred.FinishMapper;
import edu.upenn.cis455.indexer.mapred.FinishReducer;
import edu.upenn.cis455.indexer.mapred.InitMapper;
import edu.upenn.cis455.indexer.mapred.InitReducer;
import edu.upenn.cis455.indexer.mapred.IterMapper;
import edu.upenn.cis455.indexer.mapred.IterReducer;


public class AutomatedPageRankDriver 
{
	public static void main(String[] args) throws Exception 
	{
		String initInput = args[0];
		String initOutput = args[1];
		String iterInput = args[2];
		String iterOutput = args[3];
		String finishOutput = args[4];
		int numReducers = new Integer(args[5]);
		if (!run(initInput, initOutput, iterInput, iterOutput, finishOutput, numReducers))
			System.exit(1);
		System.out.println("It all worked.");
		System.exit(0);
	}

	public static boolean run(String initInput, String initOutput, String iterInput, String iterOutput, String finishOutput, int numReducers) throws Exception
	{
		Configuration conf = new Configuration();
		System.out.println("initInput: " + initInput);
		System.out.println("iterInput: " + initOutput);
		System.out.println("iterInput: " + iterInput);
		System.out.println("iterOutput: " + iterOutput);

		Job initJob = new Job(conf, "initJob");
		initJob.setJarByClass(PageRankDriver.class);
		FileInputFormat.addInputPath(initJob, new Path(initInput));
		FileOutputFormat.setOutputPath(initJob, new Path(initOutput));
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

		String currentInputDir = iterInput;
		for (int i = 0; i < PageRankDriver.NUM_ITERATIONS; i++)
		{
			conf = new Configuration();
			System.out.println("initInput: " + initInput);
			System.out.println("iterInput: " + initOutput);
			System.out.println("iterInput: " + iterInput);
			System.out.println("iterOutput: " + iterOutput);

			Job iterJob = new Job(conf, "iterJob");
			iterJob.setJarByClass(PageRankDriver.class);

			FileInputFormat.addInputPath(iterJob, new Path(currentInputDir));
			FileOutputFormat.setOutputPath(iterJob, new Path(iterOutput + "-" + i));
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
			currentInputDir = iterOutput + "-" + i;
		}
		
		conf = new Configuration();
		Job finishJob = new Job(conf, "finishJob");
		finishJob.setJarByClass(PageRankDriver.class);
		FileInputFormat.addInputPath(finishJob, new Path(currentInputDir));
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