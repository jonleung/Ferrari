package edu.upenn.cis455.indexer.mapred;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.upenn.cis455.indexer.drivers.IndexDriver;

public class ReverseIndexReducer  extends Reducer<Text, Text, Text, Text>
{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
	{
		IndexDriver.log("Inside ReverseIndexerReducer");
		String stem = new String(key.toString());
	
		int size = 0;
		ArrayList<String> vals = new ArrayList<String>();
		for (Text val : values)
		{
			vals.add(new String(val.toString()));
			size++;
		}

		double idf = 1/size;
		
		IndexDriver.log("idf: " + idf);
		IndexDriver.log("size: " + size);

		String finalOutputVal = "";
		for (String val : vals)
		{
			String[] pieces = val.split("\t");
			String url = pieces[0];
			String termFrequency = pieces[1];
			String score = pieces[2];
			String positions = pieces[3];
			double finalScore = calculateFinalScore(idf, Double.parseDouble(termFrequency), Double.parseDouble(score));
			String outputVal =  ";" + url + "\t" + finalScore + "\t" + positions;

			finalOutputVal = finalOutputVal + outputVal;
		}
		IndexDriver.log(stem + "\t" + finalOutputVal);
		context.write(new Text(stem), new Text(finalOutputVal));


	}

	private double calculateFinalScore(double idf, double tf, double score)
	{
		//TODO: calculate real score!!
		return idf + tf + score;
	}

}
