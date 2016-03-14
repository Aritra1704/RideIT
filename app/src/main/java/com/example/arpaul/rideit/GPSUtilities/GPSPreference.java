package com.example.arpaul.rideit.GPSUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class GPSPreference 
{
	private SharedPreferences preferences;
	private SharedPreferences.Editor edit;
	public static String CURRENT_LOCATION_LATTITUDE 			= 	"current_location_lattitude";
	public static String CURRENT_LOCATION_LONGITUDE 			= 	"current_location_longitude";
	
	public GPSPreference(Context context) 
	{
		preferences		=	PreferenceManager.getDefaultSharedPreferences(context);
		edit			=	preferences.edit();
	}
	
	public void saveString(String strKey,String strValue)
	{
		edit.putString(strKey, strValue);
		edit.commit();
	}
	
	public void saveInt(String strKey,int value)
	{
		edit.putInt(strKey, value);
		edit.commit();
	}
	
	public void saveLong(String strKey,Long value)
	{
		edit.putLong(strKey, value);
		edit.commit();
	}
	
	public void saveFloat(String strKey, float value)
	{
		edit.putFloat(strKey, value);
		edit.commit();
	}
	
	public void saveDouble(String strKey,String value)
	{
		edit.putString(strKey, value);
		edit.commit();
	}
	
	public void saveBoolean(String strKey,boolean value)
	{
		edit.putBoolean(strKey, value);
		edit.commit();
	}
	
	public void removeFromPreference(String strKey)
	{
		edit.remove(strKey);
	}
	
	public String getStringFromPreference(String strKey,String defaultValue)
	{
		return preferences.getString(strKey, defaultValue);
	}
	
	public boolean getbooleanFromPreference(String strKey,boolean defaultValue)
	{
		return preferences.getBoolean(strKey, defaultValue);
	}
	
	public int getIntFromPreference(String strKey,int defaultValue)
	{
		return preferences.getInt(strKey, defaultValue);
	}
	
	public long getLongFromPreference(String strKey)
	{
		return preferences.getLong(strKey, 0);
	}
	
	public float getFloatFromPreference(String strKey,float defaultValue)
	{
		return preferences.getFloat(strKey, defaultValue);
	}
	
	public double getDoubleFromPreference(String strKey,double defaultValue)
	{
		return	Double.parseDouble(preferences.getString(strKey, ""+defaultValue));
	}
}
