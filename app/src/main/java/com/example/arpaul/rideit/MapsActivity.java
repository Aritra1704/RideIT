package com.example.arpaul.rideit;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.arpaul.rideit.Common.AppConstant;
import com.example.arpaul.rideit.Common.AppPreference;
import com.example.arpaul.rideit.GPSUtilities.GPSCallback;
import com.example.arpaul.rideit.GPSUtilities.GPSErrorCode;
import com.example.arpaul.rideit.GPSUtilities.GPSUtills;
import com.example.arpaul.rideit.Receiver.MyWakefulReceiver;
import com.example.arpaul.rideit.Utilities.CalendarUtils;
import com.example.arpaul.rideit.Utilities.CustomLoader;
import com.example.arpaul.rideit.Utilities.FileUtils;
import com.example.arpaul.rideit.Utilities.LogUtils;
import com.example.arpaul.rideit.Utilities.UnCaughtException;
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
    //private CustomLoader loader;
    private Dialog updateGooglePlayServiceDialog = null;
    //Gps
    private LatLng currentLatLng = null;
    private boolean isGpsEnabled;
    private Button btnStartRide;
    private boolean ispermissionGranted = false;
    private Intent intent;
    private AlarmManager alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToast("OnCreate Exception 0");
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MapsActivity.this));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_activity);

        initialiseControls();

        showToast("OnCreate Exception 1");
        gpsUtills = GPSUtills.getInstance(MapsActivity.this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());
        gpsUtills.setListner(MapsActivity.this);
        showToast("OnCreate Exception 2");
        if(checkPermission() != 0){
            verifyLocation();
            showToast("OnCreate verify");
        }
        else{
            createGPSUtils();
            showToast("OnCreate check permission 0");
        }

        //loader = new CustomLoader(MapsActivity.this);

        btnStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ispermissionGranted){
                    if(TextUtils.isEmpty(new AppPreference(MapsActivity.this).getStringFromPreference(AppPreference.IS_STARTED,""))){
                        setupAlarm();
                        btnStartRide.setText("Stop Ride");

                        new AppPreference(MapsActivity.this).saveStringInPreference(AppPreference.IS_STARTED ,
                                "Started Ride: \n" +
                                "DateTime: "+ CalendarUtils.getCurrentDateTime()+
                                "\nLocation: Latitude: "+currentLatLng.latitude+
                                " Longitude: "+currentLatLng.longitude);
                    }
                    else {
                        gpsUtills.getCurrentLatLng();
                        stopAlarm();
                        btnStartRide.setText("Start Ride");
                        String prepareBody = new AppPreference(MapsActivity.this).getStringFromPreference(AppPreference.IS_STARTED,"")+
                                "\n\nStopped Ride: \n" +
                                "DateTime: "+ CalendarUtils.getCurrentDateTime()+
                                "\nLocation: Latitude: "+currentLatLng.latitude+
                                " Longitude: "+currentLatLng.longitude;
                        new FileUtils().writeToFileOnInternalStorage(MapsActivity.this,"RideIT.txt",prepareBody);

                        new AppPreference(MapsActivity.this).removeFromPreference(AppPreference.IS_STARTED);
                    }
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showToast("OnStart Exception 3");
        if(gpsUtills != null && ispermissionGranted){
            gpsUtills.connectGoogleApiClient();
            showToast("OnStart Connect googleAPI");

            getCurrentLocation();
        }
    }

    /*@Override
    public void onResume()
    {
        super.onResume();
        showToast("OnResume Exception 4");
        if(gpsUtills != null && ispermissionGranted && isGpsEnabled){
            showToast("OnResume start locationupdate");
            gpsUtills.startLocationUpdates();
        }
    }*/

    private int checkPermission(){
        int hasLocationPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION );
            if( hasLocationPermission == PackageManager.PERMISSION_GRANTED ) {
                ispermissionGranted = true;
            }
        } else
            ispermissionGranted = true;
        return hasLocationPermission;
    }

    private void verifyLocation(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                                            Manifest.permission.CAMERA},1);
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int hasLocationPermission = checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION );
            if( hasLocationPermission != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            } else {
                createGPSUtils();
            }
        } else {
            ispermissionGranted = true;
            createGPSUtils();
        }*/
    }

    private void createGPSUtils(){
        //Gps
        gpsUtills.isGpsProviderEnabled();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            ispermissionGranted = true;
            gpsUtills.connectGoogleApiClient();
            createGPSUtils();

            getCurrentLocation();
        }
    }

    private void getCurrentLocation(){
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                gpsUtills.getCurrentLatLng();
                showCurrentLocation();
            }
        }, 2 * 1000);
    }

    private void setupAlarm(){
        Calendar cal = Calendar.getInstance();

        intent = new Intent(this, MyWakefulReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this,1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AppConstant.REPEAT_TIME, pintent);
        showToast("Setup Alarm Start service: "+AppConstant.FOLDER_Directory+"/"+AppConstant.FOLDER_PATH);
    }

    private void stopAlarm(){
        //Intent intent = new Intent(this, MyWakefulReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this,1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        showToast("stop Alarm stop service");
    }

    private void initialiseControls(){
        btnStartRide = (Button) findViewById(R.id.btnStartRide);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mMap==null)
            mapFragment.getMapAsync(this);
        //mapFragment.getMapAsync(this);
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

        if(gpsUtills != null && ispermissionGranted && !isGpsEnabled){
            gpsUtills.isGpsProviderEnabled();
            showToast("OnMapReady isprovider enabled");
        }
        if(isGpsEnabled)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //loader.hideLoader();
                    gpsUtills.getCurrentLatLng();
                    showCurrentLocation();
                }
            }, 2 * 1000);
        }
        else if(ispermissionGranted)
        {
            showSettingsAlert();
        }
    }

    private void showCurrentLocation(){
        if(currentLatLng != null && currentLatLng.latitude > 0.0 && currentLatLng.longitude > 0.0)
        {
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

    @Override
    public void gotGpsValidationResponse(Object response, GPSErrorCode code)
    {
        if(code == GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED) {
            isGpsEnabled = false;
            showSettingsAlert();
        }
        else if(code == GPSErrorCode.EC_GPS_PROVIDER_ENABLED) {
            isGpsEnabled = true;
            gpsUtills.getCurrentLatLng();
        }
        else if(code == GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION) {
            currentLatLng = (LatLng) response;
        }
        else if(code == GPSErrorCode.EC_LOCATION_FOUND) {
            currentLatLng = (LatLng) response;
            LogUtils.debug("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);

            //loader.hideLoader();
            showCurrentLocation();
            gpsUtills.stopLocationUpdates();
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID)
        {
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD)
        {
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(gpsUtills != null)
            gpsUtills.disConnectGoogleApiClient();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(gpsUtills != null)
            gpsUtills.stopLocationUpdates();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(gpsUtills != null)
            gpsUtills.stopLocationUpdates();
    }

    public void showSettingsAlert()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(builder == null || !isDialogShowing){
                    isDialogShowing = true;
                    showCustomDialog("GPS Settings","GPS is not enabled.So please enable GPS for better Location.","Settings",null,false);
                }
            }
        });
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

    AlertDialog.Builder builder;
    boolean isDialogShowing = false;
    private void showCustomDialog(String title,String message, String positiveButton, String negativeButton, boolean isCancelable){

        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                isGpsEnabled = true;
                dialog.cancel();
            }
        });
        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(isCancelable);
        builder.show();
    }

    private void showToast(String message){
        Toast.makeText(MapsActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
