package com.example.arpaul.rideit.GPSUtilities;

import java.text.SimpleDateFormat;

public class GPSCalendarUtils 
{
   
	public static final String DATE_STD_PATTERN  = "yyyy-MM-dd HH:mm a";
	public static final String DATE_STD_PATTERN2 = "dd-MM-yyyy HH:mm ";
	
	public static String getCurrentDate() 
	{
		String sdf = new SimpleDateFormat(DATE_STD_PATTERN).format(System.currentTimeMillis());
		return sdf;
	}
	
	public static String getCurrentDateForLogs() 
	{
		String sdf = new SimpleDateFormat(DATE_STD_PATTERN2).format(System.currentTimeMillis());
		return sdf;
	}
	
}
