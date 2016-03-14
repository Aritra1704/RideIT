package com.example.arpaul.rideit.GPSUtilities;


public class GPSConstants 
{
	public static final double DISTANCE_VALIDATION_RANGE     = 50;           //50 meters.
	public static final double EARTH_RADIUS                  = 3958.75;
	public static final int METER_CONVERSION                 = 1609;
	
	//location updates
	public static final int MAX_RESULTS                      = 1;
	public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;            // 10 meters.
	public static final long MIN_TIME_BW_UPDATES             = 1000 * 60 * 1; // 1 minute.
	
	//timer task
	public static final int TIMER_TASK_DELAY                 = 1*1000;      //1 second.
	public static final int TIMER_TASK_PERIOD                = 2*1000;     //2 seconds.
	
	//location updates
	public static final long INTERVAL                       = 3*1000;  //3 seconds.
	public static final long FASTEST_INTERVAL               = 1*1000;  //3 seconds.
	
	
	
}
