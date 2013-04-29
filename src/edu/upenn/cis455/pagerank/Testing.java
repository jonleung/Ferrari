package edu.upenn.cis455.pagerank;

public class Testing 
{
	
	public static String arrayToString(String[] array, String separator)
	{
		String toReturn = "";
		for (String s : array)
		{
			toReturn = toReturn + s + separator;
		}
		if (toReturn.endsWith(separator))
			toReturn = toReturn.substring(0, toReturn.length()-separator.length());
		return toReturn;
	}
	
	public static void main (String[] args)
	{
		String[] array = {"one", "two", "three"};
		System.out.println(arrayToString(array, "###"));
	}

}
