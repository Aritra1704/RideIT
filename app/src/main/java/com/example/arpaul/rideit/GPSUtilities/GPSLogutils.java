package com.example.arpaul.rideit.GPSUtilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class GPSLogutils 
{
	private static boolean isLogEnabled = true;
	
	private static String packageName = "";

	public GPSLogutils() {
	}
	
	public static boolean isLogEnabled()
	{
		return isLogEnabled;
	}
	
	public static void setLogEnable(boolean isLogEnabled)
	{
		GPSLogutils.isLogEnabled = isLogEnabled;
	}
	
	public static void setPackgeName(String packageName)
	{
		GPSLogutils.packageName = packageName;
	}
	
	public static void error(String tag, String msg)
	{
		//Errors should be print, no need this condition
		if(isLogEnabled)
		{
			if(msg != null)			
				Log.e(tag, msg);
			else
				Log.e(tag, "null"); 
		}
	}
	
	public static void info(String tag, String msg)
	{
		if(isLogEnabled)
		{
			if(msg != null)			
				Log.i(tag, msg);
			else
				Log.i(tag, "null");
		}
	}
	
	public static void verbose(String tag, String msg)
	{
		if(isLogEnabled)
		{
			if(msg != null)			
				Log.v(tag, msg);
			else
				Log.v(tag, "null");
		}
	}
	
	public static void debug(String tag, String msg)
	{
		if(isLogEnabled)
		{
			if(msg != null)			
				Log.d(tag, msg);
			else
				Log.d(tag, "null");
		}
	}
	
	 public static void writeSdcardLog(String str,String filename) 
	 {
		if(isLogEnabled)
		{
			try 
			{
				FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()
						+ "/GPSValidationLib"+filename+".txt", true);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(str.getBytes());
				
				bos.flush();
				bos.close();
				fos.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	 
	 public static void writeToAppDir(String str,String filePath) 
	 {
		if(isLogEnabled)
		{
			try 
			{   
				FileOutputStream fos = new FileOutputStream(filePath, true);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(str.getBytes());
				
				bos.flush();
				bos.close();
				fos.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	 
	 public static void writeSdcard(String str) 
	 {
		if(isLogEnabled)
		{
			try 
			{
				//Environment.getExternalStorageDirectory()+"/GpsTracker_"+CalendarUtils.getCurrentDate()+".txt
				FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/GPSValidationLib.txt", true);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(str.getBytes());
				
				bos.flush();
				bos.close();
				fos.close();
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    } 	 
	 
	public static void createLogDataForLib(String methodName,String response,String errorCode)
	{
		if(isLogEnabled)
		{
			String fileLogPath = "data/data/"+packageName+"/GPSValidationLib.txt";
			String timeStamp = GPSCalendarUtils.getCurrentDateForLogs();
			writeToAppDir("\nMethod: "+methodName+" , Response :"+response+"  "
					+ "\nErrorCode: "+errorCode+" , TimeStamp: "+timeStamp+
					"\n===================================================",fileLogPath);
		}
	}
     
	public static void createLogDataForApp(String action,String userId,String siteId,String response)
	{
		if(isLogEnabled)
		{
			//String logFilePath = "data/data/com.wint.gpsutills.app/GpsTracker_"+CalendarUtils.getCurrentDate()+".txt";
			String logFilePath = "data/data/"+packageName+"/SFA_GPS_Logs.txt";
			String timeStamp = GPSCalendarUtils.getCurrentDateForLogs();
			writeToAppDir("\nAction: "+action+" , Info :"+response+"  "
					+ "\nUserId: "+userId+" , siteId: "+siteId+" , TimeStamp: "+timeStamp+
					"\n===================================================",logFilePath);
		}
	}
	
	public static void createLogDataForApp(String logString)
	{
		if(isLogEnabled)
		{
			//String logFilePath = "data/data/com.wint.gpsutills.app/GpsTracker_"+CalendarUtils.getCurrentDate()+".txt";
			String logFilePath = "data/data/"+packageName+"/SFA_GPS_Logs.txt";
			String timeStamp = GPSCalendarUtils.getCurrentDateForLogs();
			writeToAppDir(logString+", TimeStamp: "+timeStamp+"\n",logFilePath);
		}
	}
	
	public static void copyFromAppDirToSdcard(String source, String destination)
			throws IOException 
	{
          GPSLogutils.debug("GPSTrack", "source:"+source+", Destination: "+destination);
		  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
		    source));
		  FileOutputStream fos = new FileOutputStream(destination);
		  BufferedOutputStream bos = new BufferedOutputStream(fos);
		  byte byt[] = new byte[8192];
		  int noBytes;
		  while ((noBytes = bis.read(byt)) != -1)
		   bos.write(byt, 0, noBytes);

		  bos.flush();
		  bos.close();
		  fos.close();
		  bis.close();
   }
	
}
