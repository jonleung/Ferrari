package edu.upenn.cis455.indexer.mapred;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.upenn.cis455.indexer.drivers.IndexDriver;

public class ForwardIndexReducer extends Reducer<Text, Text, Text, Text>
{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
	{
		IndexDriver.log("Inside ReverseIndexerReducer");
		HashMap<String, ArrayList<String>> stems = new HashMap<String, ArrayList<String>>();
		
		String url = new String(key.toString());
		for (Text value : values)
		{
			String val = new String(value.toString());
			String stem = val.split("  ")[0];
			ArrayList<String> current;
			if (stems.containsKey(stem))
				current = stems.remove(stem);
			else
				current = new ArrayList<String>();
			current.add(val);
			stems.put(stem, current);
		}
		for (Map.Entry<String, ArrayList<String>> entry : stems.entrySet())
		{
			String currentStem = entry.getKey();
			ArrayList<String> currentVal = entry.getValue();
			int termFrequency = currentVal.size();
			String scoreAndPositions = getScoreAndPositions(currentVal);
			String outputVal = currentStem + "\t" + termFrequency + "\t" + scoreAndPositions;
			IndexDriver.log(url + "\t" + outputVal);
			context.write(new Text(url), new Text(outputVal));
		}
		
	}
	
	private String getScoreAndPositions(ArrayList<String> values)
	{
		double score = 0;
		String positions = "";
		for (String val : values)
		{
			String[] pieces = val.split("  ");
			Double relativeFontSize = Double.parseDouble(pieces[2]);
			String isFancy = pieces[4];
			if (isFancy.equals("true"))
				score += 10;
			else
				score += 1;
			score += relativeFontSize;
			positions = positions + pieces[3] + ",";
		}
		return score + "\t" + positions;
	}

}
