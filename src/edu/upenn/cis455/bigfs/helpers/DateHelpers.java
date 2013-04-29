package edu.upenn.cis455.bigfs.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelpers {

	
	
	public static String currentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("m/dd_hh;mm;ssa");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
}
