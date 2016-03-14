package com.example.arpaul.rideit.GPSUtilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSUtills 
{
	
	private String TAG     = "GPSTrack";
	private Context context;
	
	private GPSTrackerService gpsTrackerService;
	private GPSCallback gpsCallback;
	private static GPSUtills gpsUtills;
    
	private List<Address> address;
	private LatLng currentLatLng;
	
    	
	public synchronized static GPSUtills getInstance(Context context) 
	{
		if (gpsUtills == null) 
		{
			gpsUtills = new GPSUtills(context);
		}
		return gpsUtills;
	}
	
	private GPSUtills(Context context) 
	{
		this.context  = context;
		gpsTrackerService = new GPSTrackerService(context);	
	}
	
	public void setListner(GPSCallback gpsCallback) 
	{
		 this.gpsCallback = gpsCallback;
	}
	 
	 /**
	  * method     :isDeviceConfiguredProperly()
	  * parameters :null
	  */
     public void isDeviceConfiguredProperly()
     {
    	 boolean isGpsFeatureAvailbleOnDevice = isGpsFeatureAvailableOnDevice();
    	 if(isGpsFeatureAvailbleOnDevice)
    	 {
    		 GPSLogutils.createLogDataForLib("isDeviceConfiguredProperly", "Gps Feature Available On Device", "EC_GPS_HARDWARE_SETUP_AVAILABLE_ONDEVICE");
    		 boolean checkGooglePlayServices = checkGooglePlayServices();
    		 if(checkGooglePlayServices)
    		 {
    			 gpsCallback.gotGpsValidationResponse(checkGooglePlayServices, GPSErrorCode.EC_DEVICE_CONFIGURED_PROPERLY);
    			 GPSLogutils.createLogDataForLib("isDeviceConfiguredProperly", "Updated GooglePlay Services Available", "EC_DEVICE_CONFIGURED_PROPERLY");
    		 }
    		 else
    		 {
    			 gpsCallback.gotGpsValidationResponse(checkGooglePlayServices, GPSErrorCode.EC_GOOGLEPLAY_SERVICES_UPDATE_REQUIRED);
    			 GPSLogutils.createLogDataForLib("isDeviceConfiguredProperly", "Updated GooglePlay Services Not Available", "EC_GOOGLEPLAY_SERVICES_UPDATE_REQUIRED");
    		 }
    		 
    	 }
    	 else
    	 {
    		 gpsCallback.gotGpsValidationResponse(isGpsFeatureAvailbleOnDevice, GPSErrorCode.EC_GPS_HARDWARE_SETUP_NOTAVAILABLE_ONDEVICE);
    		 GPSLogutils.createLogDataForLib("isDeviceConfiguredProperly", "Gps Feature Not Available On Device", "EC_GPS_HARDWARE_SETUP_NOTAVAILABLE_ONDEVICE");
    	 }
     }
	  
     /**
	  * method     :isGpsProviderEnabled()
	  * parameters :null
	  */
     public void isGpsProviderEnabled()
     {
    	 boolean isGpsProviderEnabled;
    	 LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	 isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    	 if(isGpsProviderEnabled)
    	 {
    		 gpsCallback.gotGpsValidationResponse(isGpsProviderEnabled, GPSErrorCode.EC_GPS_PROVIDER_ENABLED);
    		 GPSLogutils.createLogDataForLib("isGpsProviderEnabled", "Gps Provider Enabled", "EC_GPS_PROVIDER_ENABLED");
    		 
    	 }
    	 else
    	 {
    		 gpsCallback.gotGpsValidationResponse(isGpsProviderEnabled, GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED);
    		 GPSLogutils.createLogDataForLib("isGpsProviderEnabled", "Gps Provider Not Enabled", "EC_GPS_PROVIDER_NOT_ENABLED");
    	 }
     }
     
     
     /**
	  * method     :isNetworkProviderEnabled()
	  * parameters :null
	  */
     public void isNetworkProviderEnabled()
     {
    	 boolean isNetworkProviderEnabled;
    	 LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    	 isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    	 if(isNetworkProviderEnabled)
    	 {
    		gpsCallback.gotGpsValidationResponse(isNetworkProviderEnabled, GPSErrorCode.EC_NETWORK_PROVIDER_ENABLED);
    		GPSLogutils.createLogDataForLib("isNetworkProviderEnabled", "Network Provider Enabled", "EC_NETWORK_PROVIDER_ENABLED");
    	 }
    	 else
    	 {
    		 gpsCallback.gotGpsValidationResponse(isNetworkProviderEnabled, GPSErrorCode.EC_NETWORK_PROVIDER_NOT_ENABLED);
    		 GPSLogutils.createLogDataForLib("isNetworkProviderEnabled", "Network Provider Not Enabled", "EC_NETWORK_PROVIDER_NOT_ENABLED");
    	 }
     }
     
     
     /**
	  * method     :isInternetConnectionAvailable()
	  * parameters :null
	  */ 
     public void isInternetConnectionAvailable()
 	 {
 		boolean isNetworkConnectionAvailable = false ;
 		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
 		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
 		if(activeNetworkInfo != null) 
 		    isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
 		if(isNetworkConnectionAvailable)
 		{
 			gpsCallback.gotGpsValidationResponse(isNetworkConnectionAvailable, GPSErrorCode.EC_INTERNETCONNECTION_AVAILABLE);
 			GPSLogutils.createLogDataForLib("isInternetConnectionAvailable", "Internet Connection Available", "EC_INTERNETCONNECTION_AVAILABLE");
 		}
 		else
 		{
 			gpsCallback.gotGpsValidationResponse(isNetworkConnectionAvailable, GPSErrorCode.EC_INTERNETCONNECTION_NOT_AVAILABLE);
 			GPSLogutils.createLogDataForLib("isInternetConnectionAvailable", "Internet Connection Not Available", "EC_INTERNETCONNECTION_NOT_AVAILABLE");
 		}
 	 }
     
     /**
	  * method                :isCustomerLocationVaild()
	  * @param currentLatLng  :current location lattitude , longitude values
	  * @param customerLatLng :customer location lattitude , longitude values
	  */ 
     public void isCustomerLocationVaild(LatLng currentLatLng,LatLng customerLatLng)
     {
    	double actualDistance = calculateDistance(currentLatLng, customerLatLng);
    	 
    	 if(actualDistance <= GPSConstants.DISTANCE_VALIDATION_RANGE)
    	 {
    		 gpsCallback.gotGpsValidationResponse(true, GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID);
    		 GPSLogutils.createLogDataForLib("isCustomerLocationVaild", "Customer Location Vaild, "
    		 + "CurrentLat: "+currentLatLng.latitude+", CurrentLong: "+currentLatLng.longitude+"CustomerLat: "+customerLatLng.latitude+"CustomerLong: "+currentLatLng.longitude+"Actual Distance"+actualDistance,"EC_CUSTOMER_LOCATION_IS_VALID");
                 		 
    	 }
    	 else
    	 {
    		 gpsCallback.gotGpsValidationResponse(false, GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD);
    		 GPSLogutils.createLogDataForLib("isCustomerLocationVaild", "Customer Location Vaild Fail,"
    	     + "CurrentLat: "+currentLatLng.latitude+", CurrentLong: "+currentLatLng.longitude+"CustomerLat: "+customerLatLng.latitude+"CustomerLong: "+currentLatLng.longitude+"Actual Distance"+actualDistance,"EC_CUSTOMER_LOCATION_IS_VALID");
    	 }
     }
     
     /*
 	 * method           : getAddressFromLatLng()
 	 * @param lattitude : lattitude value of location.
 	 * @param longitude : longitude value of location.
 	 * return           : null
  	 */
 	 public void getAddressFromLatLng(double lattitude,double longitude)
 	 {
 	  	try 
 	  	{
 	  	   //internet connection is  mandatory for getting address from lattitude,longitude.	
 	  	   String addr = "";
 	  	   Geocoder geocoder = new Geocoder(context,Locale.getDefault());
 		   address	= geocoder.getFromLocation(lattitude, longitude, GPSConstants.MAX_RESULTS); 
 		   if(address !=null)
 		   {	
 		     for(int i =0;i < address.get(0).getMaxAddressLineIndex();i++)
 		       addr+=address.get(0).getAddressLine(i) + "\n";
 		 	 gpsCallback.gotGpsValidationResponse(addr, GPSErrorCode.EC_ADDRESS_FOUND);
 		 	 GPSLogutils.createLogDataForLib("getAddressFromLatLng", "Address found", "EC_ADDRESS_FOUND");
 		 	 
 		   }	
 		   else
 		   {
 			   gpsCallback.gotGpsValidationResponse(addr, GPSErrorCode.EC_NO_ADDRESS_FOUND);
 			   GPSLogutils.createLogDataForLib("getAddressFromLatLng", "Address Not found", "EC_NO_ADDRESS_FOUND");
 		   }
 		} 
 	  	catch (IOException e) 
 	  	{
 			e.printStackTrace();
 		}
 		
 	 }
 	 
 	 /*
 	  * method  : getLatLngFromAddress()
 	  * @param  : locationAddress
 	  */
 	 public void getLatLngFromAddress(String locationAddress)
 	 {
 		 try 
 		 {
 			  //internet connection is  mandatory for getting lattitude,longitude from address.
 			  Geocoder geocoder = new Geocoder(context, Locale.getDefault());
 			  List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
 			  if (addressList != null && addressList.size() > 0) 
 			  {
 				  LatLng latLng = null;
                   Address address = addressList.get(0);
                   double lattitude = address.getLatitude();
                   double longitude = address.getLongitude();
                   latLng = new LatLng(lattitude, longitude);
                   if(lattitude == 0.0 && longitude == 0.0)
                   {
                 	  gpsCallback.gotGpsValidationResponse(latLng, GPSErrorCode.EC_NO_LATLONG_FOUND);
                 	  GPSLogutils.createLogDataForLib("getLatLngFromAddress", "LatLong Not found", "EC_NO_LATLONG_FOUND");
                   }
                   else
                   {
                 	  gpsCallback.gotGpsValidationResponse(latLng, GPSErrorCode.EC_LATLONG_FOUND);
                 	  GPSLogutils.createLogDataForLib("getLatLngFromAddress", "LatLong  found", "EC_LATLONG_FOUND");
                   }
               }
 			  
 			 
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 	 }
     
     //method to check Supported GooglePlayServices avail in Device or not.
     private boolean checkGooglePlayServices() 
     {
         int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
         if (status != ConnectionResult.SUCCESS) 
         {
             GPSLogutils.debug(TAG, GooglePlayServicesUtil.getErrorString(status));
             return false;
         }
         else 
         {
        	 // google play services is updated. 
         	 GPSLogutils.debug(TAG, GooglePlayServicesUtil.getErrorString(status));
             return true;
         }
     }
     
     //method to check Gps Hardware setUp availble or not in Device.
     private boolean isGpsFeatureAvailableOnDevice()
     {
    	 PackageManager packageManager = context.getPackageManager();
    	 boolean hasGps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    	 return hasGps;
     }
     
     private boolean isNetworkFeatureAvailableOnDevice()
     {
    	 PackageManager packageManager = context.getPackageManager();
    	 boolean hasGps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);
    	 return hasGps;
     }
      
     
  	 //Haversine formula to calculate distance between two latlngs.
  	private static double calculateDistance(LatLng currentLatLng,LatLng customerLatLng) 
    {
  		
  		double startLat  = currentLatLng.latitude;	
  	    double startLong = currentLatLng.longitude;
  	      
  	    double endLat  = customerLatLng.latitude;	
  	    double endLong = customerLatLng.longitude;
  		
  		float R = 6371; // Radius of the earth in km
  		double dLat = deg2rad(endLat-startLat);  // deg2rad below
  		double dLon = deg2rad(endLong-startLong); 
  		double a = 
  		    Math.sin(dLat/2) * Math.sin(dLat/2) +
  		    Math.cos(deg2rad(startLat)) * Math.cos(deg2rad(endLat)) * 
  		    Math.sin(dLon/2) * Math.sin(dLon/2)
  		    ; 
  		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  		double d = R * c; // Distance in km.
  		return d*1000; //Distance in meters.
  		
    }
     	 
	 private static double rad2deg(double rad) 
	 {
		 return (rad * 180.0 / Math.PI);

	 }
	    
	 private static double deg2rad(double deg) 
	 {
		 return (deg * Math.PI / 180.0);
		 
	 } 
	
	 //******************************* Log Methods *************************************//
	 
	 //method to set enable/disble logs
	 public void setLogEnable(boolean isLogEnable)
	 {
		 GPSLogutils.setLogEnable(isLogEnable);
	 }
	 
	 //method to set packagename which will useful to create logs in application dir. 
	 public void setPackegeName(String packageName)
	 {
		 GPSLogutils.setPackgeName(packageName);
	 }
	 
	 //method to create Logs into app dir.
	 public void createdLogDataForApp(String logString)
	 {
		 GPSLogutils.createLogDataForApp(logString);
	 }
	 
	 //method to create Logs into app dir.
	 public void createdLogDataForApp(String action,String userId,String siteId,String response)
	 {
		 GPSLogutils.createLogDataForApp(action, userId,siteId,response);
	 }
	 
	 //method to copy log data from app dir to sdcard.
	 public void copyAppLogDataToSdCard(String packageName)
	 {
		 GPSLogutils.debug("GPSTrack", "copyDataFromAppDirToDevice");
		 String appDirPath = "data/data/"+packageName+"/SFA_GPS_Logs.txt";
		 String sdCardPath = Environment.getExternalStorageDirectory()+"/SFA_GPS_Logs.txt";
		 try {
			GPSLogutils.copyFromAppDirToSdcard(appDirPath, sdCardPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 public void copyLibLogDataToSdCard(String packageName)
	 {
		 GPSLogutils.debug("GPSTrack", "copyDataFromAppDirToDevice");
		 String appDirPath = "data/data/"+packageName+"/GPSValidationLib.txt";
		 String sdCardPath = Environment.getExternalStorageDirectory()+"/GPSValidationLib.txt";
		 try {
			GPSLogutils.copyFromAppDirToSdcard(appDirPath, sdCardPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	//**********************************Location Updates Methods*******************************************************//
	 public void connectGoogleApiClient()
	 {
		  gpsTrackerService.connectGoogleApiClient();
	 }
	 public void disConnectGoogleApiClient()
	 {
		 gpsTrackerService.disConnectGoogleApiClient();
	 }
	 public GoogleApiClient getGoogleApiClient()
	 {
		return gpsTrackerService.getGoogleApiClient();
	 }
	 
	 public void startLocationUpdates()
	 {
		 if(getGoogleApiClient().isConnected()) 
	     {
			 gpsTrackerService.startLocationUpdates();
	     }
	 }
	 
	 public void stopLocationUpdates()
	 {
		 gpsTrackerService.stopLocationUpdates();
	 }
	 
	 /*
	  * method  : getCurrentLatLng
	  * params  : null
	  * return  : LatLng of current location
	  */
	 public void getCurrentLatLng()
	 {
		 currentLatLng = gpsTrackerService.getLatLng();
		 if(currentLatLng.latitude == 0.0 && currentLatLng.longitude == 0.0)
		 {
			 gpsCallback.gotGpsValidationResponse(currentLatLng, GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION);
			 GPSLogutils.createLogDataForLib("getCurrentLatLng", "lattitude : "+currentLatLng.latitude+", "+currentLatLng.longitude, "EC_UNABLE_TO_FIND_LOCATION");
		 }
		 else
		 {
			 gpsCallback.gotGpsValidationResponse(currentLatLng, GPSErrorCode.EC_LOCATION_FOUND);
			 GPSLogutils.createLogDataForLib("getCurrentLatLng", "lattitude : "+currentLatLng.latitude+", "+currentLatLng.longitude, "EC_LOCATION_FOUND");
		 }
	 }
	 
	 public void startTimer()
	 {
		 gpsTrackerService.startTimer();
	 }
	 
	 public void stopTimer()
	 {
		 gpsTrackerService.stoptimertask();
	 }
	 
}
