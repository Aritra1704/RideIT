package com.example.arpaul.rideit.GPSUtilities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GPSTrackerService extends Service implements LocationListener,GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener
{
	private static final String TAG = "GPSTrack";
    	
	private String mLastUpdateTime;
	private Context mContext;
	private GPSPreference gpsPreference;
	 
	private LocationRequest mLocationRequest;
	private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

	private Timer timer;
	private TimerTask timerTask;
	private Handler handler = new Handler();

    
	public GPSTrackerService(Context mContext) 
	{
		this.mContext = mContext;
		gpsPreference = new GPSPreference(mContext);
		createLocationRequest();
		createGoogleApiClient();
	}
	
	 public void createLocationRequest() 
	 {
		mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(GPSConstants.INTERVAL);
        mLocationRequest.setFastestInterval(GPSConstants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        GPSLogutils.debug(TAG, "LocationRequest Created");
	 }
	 
	 public void createGoogleApiClient()
	 {
		 mGoogleApiClient = new GoogleApiClient.Builder(mContext)
	     .addApi(LocationServices.API)
	     .addConnectionCallbacks(this)
	     .addOnConnectionFailedListener(this)
	     .build();
		 GPSLogutils.debug(TAG, "GoogleApi Client Created");
	 }
	 
	 public void startLocationUpdates() 
	 {
		if(mGoogleApiClient != null && mLocationRequest != null)
		{
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
			GPSLogutils.debug(TAG, "Location update started");
		}
			
	}
	 
	 public void stopLocationUpdates() 
	 {
		if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			GPSLogutils.debug(TAG, "Location update stopped ");
			
		}
	 }
	
	public void connectGoogleApiClient()
	{
		if(mGoogleApiClient != null)
		{
			mGoogleApiClient.connect();
			GPSLogutils.debug(TAG, "GoogleApi Client connected");
		}
	}
	
	public void disConnectGoogleApiClient()
	{
		if(mGoogleApiClient != null)
		{
			mGoogleApiClient.disconnect();
			GPSLogutils.debug(TAG, "GoogleApi Client disConnected");
			stopSelf();
			GPSLogutils.debug(TAG, "GPSTrackerService stopped");
			
		}
	}

	public GoogleApiClient getGoogleApiClient()
	{
		return mGoogleApiClient;
	}
	
	public LatLng getLatLng()
	{
		String lattitude = gpsPreference.getStringFromPreference(GPSPreference.CURRENT_LOCATION_LATTITUDE, "0.0");
		String longitude = gpsPreference.getStringFromPreference(GPSPreference.CURRENT_LOCATION_LONGITUDE, "0.0");
		LatLng latlng = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));
		return latlng;
	}
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	
	public void startTimer() 
	{
		timer = new Timer();
		timerTask = new TimerTask() {
			public void run() 
			{
				handler.post(new Runnable() {
					public void run() 
					{
						LatLng latLng = getLatLng();
						GPSLogutils.debug(TAG, "lattitude: "+latLng.latitude+", longitude: "+latLng.longitude);
					}
				});
			}
		};
		
		
		timer.schedule(timerTask, GPSConstants.TIMER_TASK_DELAY, GPSConstants.TIMER_TASK_PERIOD); 
	}
	
	public void stoptimertask() 
	{
		if (timer != null) 
		{
			timer.cancel();
			timer = null;
		}
	}
	
	@SuppressLint("ShowToast")
	@Override
	public void onLocationChanged(Location location) 
	{
		//Location Listener.
		GPSLogutils.error(TAG, "Firing onLocationChanged Callback");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        
        if (mCurrentLocation != null) 
        {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            
            gpsPreference.saveString(GPSPreference.CURRENT_LOCATION_LATTITUDE, lat);
            gpsPreference.saveString(GPSPreference.CURRENT_LOCATION_LONGITUDE, lng);
            
            /*GPSLogutils.error(TAG, "At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider()+""
                    		+ "\n===================================================");*/
            
        }
        else 
        {
        	GPSLogutils.error(TAG, "location is null ");
             
        }
        
        
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		GPSLogutils.error(TAG, "onConnected - isConnected : " + mGoogleApiClient.isConnected());
	    startLocationUpdates();
		
	}

	@Override
	public void onConnectionSuspended(int i) 
	{
       		
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) 
	{
		GPSLogutils.error(TAG, "Connection failed: " + connectionResult.toString());
	}

}
