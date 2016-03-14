package com.example.arpaul.rideit;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arpaul.rideit.Common.AppConstant;
import com.example.arpaul.rideit.GPSUtilities.GPSCallback;
import com.example.arpaul.rideit.GPSUtilities.GPSErrorCode;
import com.example.arpaul.rideit.GPSUtilities.GPSUtills;
import com.example.arpaul.rideit.Receiver.MyWakefulReceiver;
import com.example.arpaul.rideit.Utilities.CustomLoader;
import com.example.arpaul.rideit.Utilities.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GPSCallback {

    private GoogleMap mMap;
    private GPSUtills gpsUtills;
    private CustomLoader loader;
    private Dialog updateGooglePlayServiceDialog = null;
    //Gps
    private LatLng currentLatLng = null;
    //private boolean isLocationFound;
    private boolean isGpsProviderEnabled;
    private Button btnStartRide;
    private boolean rideStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        initialiseControls();

        //Gps
        gpsUtills = GPSUtills.getInstance(MapsActivity.this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());

        gpsUtills.setListner(MapsActivity.this);
        gpsUtills.isGpsProviderEnabled();

        loader = new CustomLoader(MapsActivity.this);

        btnStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rideStarted){
                    setupAlarm();
                    btnStartRide.setText("Stop Ride");
                }
                else {
                    stopAlarm();
                    btnStartRide.setText("Start Ride");
                }
            }
        });
    }

    Intent intent;
    private void setupAlarm(){
        Calendar cal = Calendar.getInstance();

        //Intent intent = new Intent(this, CameraService.class);
        //PendingIntent pintent = PendingIntent.getService(this, 1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        intent = new Intent(this, MyWakefulReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this,1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
// schedule for every 30 seconds
        /*alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pintent);*/

        //cal.add(Calendar.SECOND, 30);
        //
        // Fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AppConstant.REPEAT_TIME, pintent);
    }

    private void stopAlarm(){
        Intent intent = new Intent(this, CameraService.class);
        //PendingIntent pintent = PendingIntent.getService(this, 1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pintent = PendingIntent.getBroadcast(this,1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    private void initialiseControls(){
        btnStartRide = (Button) findViewById(R.id.btnStartRide);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gpsUtills.isGpsProviderEnabled();
        if(isGpsProviderEnabled)
        {
            //loader.showLoader("Please wait..");

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    loader.hideLoader();
                    gpsUtills.getCurrentLatLng();
                    if(currentLatLng.latitude > 0.0 && currentLatLng.longitude > 0.0)
                    {
                        String latLng = currentLatLng.latitude+","+currentLatLng.longitude;
                        if(mMap!=null)
                        {
                            mMap.clear();
                            MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));
                            markerOptions.title("Your Location");
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16.0f));
                        }
                    }
                    else
                        Toast.makeText(MapsActivity.this, "Unable to fetch your current location please try again.", Toast.LENGTH_SHORT).show();
                }
            }, 2 * 1000);
        }
        else
        {
            showSettingsAlert();
        }
    }

    @Override
    public void gotGpsValidationResponse(Object response, GPSErrorCode code)
    {
        if(code == GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED)
        {
            isGpsProviderEnabled = false;
            //showSettingsAlert();
        }
        else if(code == GPSErrorCode.EC_GPS_PROVIDER_ENABLED)
        {
            isGpsProviderEnabled = true;
        }
        else if(code == GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION)
        {
            currentLatLng = (LatLng) response;
        }
        else if(code == GPSErrorCode.EC_LOCATION_FOUND)
        {
            currentLatLng = (LatLng) response;
            LogUtils.debug("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);

            loader.hideLoader();
            if(currentLatLng.latitude > 0.0 && currentLatLng.longitude > 0.0)
            {
                String latLng = currentLatLng.latitude+","+currentLatLng.longitude;
                if(mMap!=null)
                {
                    mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));
                    markerOptions.title("Your Location");
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16.0f));

                    gpsUtills.stopLocationUpdates();
                }
            }
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID)
        {
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD)
        {
        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        gpsUtills.connectGoogleApiClient();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        gpsUtills.disConnectGoogleApiClient();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gpsUtills.stopLocationUpdates();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        gpsUtills.startLocationUpdates();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        gpsUtills.stopLocationUpdates();
    }

    public void showSettingsAlert()
    {
        Toast.makeText(MapsActivity.this, "GPS is not enabled.So please enable GPS for better Location.", Toast.LENGTH_LONG).show();
    }

    public void showGoogleUpdateServiceAlert()
    {

        updateGooglePlayServiceDialog = null;

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);
        if(status != ConnectionResult.SUCCESS)
        {
            updateGooglePlayServiceDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if(!isFinishing())
                updateGooglePlayServiceDialog.show();
        }
        else
        {
            Toast.makeText(MapsActivity.this, "You have updated googlePlayservice already", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelGoogleUpdateServiceAlert()
    {
        if(updateGooglePlayServiceDialog != null && updateGooglePlayServiceDialog.isShowing())
        {
            updateGooglePlayServiceDialog.dismiss();
        }
    }
}
