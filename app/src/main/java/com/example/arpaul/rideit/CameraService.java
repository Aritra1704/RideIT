package com.example.arpaul.rideit;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.arpaul.rideit.Common.AppConstant;
import com.example.arpaul.rideit.GPSUtilities.GPSCallback;
import com.example.arpaul.rideit.GPSUtilities.GPSErrorCode;
import com.example.arpaul.rideit.GPSUtilities.GPSUtills;
import com.example.arpaul.rideit.Receiver.MyWakefulReceiver;
import com.example.arpaul.rideit.Utilities.CameraController;
import com.example.arpaul.rideit.Utilities.CustomLoader;
import com.example.arpaul.rideit.Utilities.LogUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CameraService extends Service implements GPSCallback {

    private GPSUtills gpsUtills;
    //Gps
    private LatLng currentLatLng = null;
    private boolean isLocationFound;
    private boolean isGpsProviderEnabled;
    private boolean isPhotoTaken = false;
    private CameraController camera;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String FOLDER_PATH = "RideIT";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //Gps
        gpsUtills = GPSUtills.getInstance(CameraService.this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());

        gpsUtills.setListner(CameraService.this);
        gpsUtills.isGpsProviderEnabled();
        gpsUtills.connectGoogleApiClient();
        gpsUtills.startLocationUpdates();

        camera = new CameraController(CameraService.this,FOLDER_PATH);

    }

    /*private void setupAlarm(){
        Calendar cal = Calendar.getInstance();

        //Intent intent = new Intent(this, CameraService.class);
        //PendingIntent pintent = PendingIntent.getService(this, 1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(this, MyWakefulReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this,1201, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
// schedule for every 30 seconds
        *//*alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pintent);*//*

        //cal.add(Calendar.SECOND, 30);
        //
        // Fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AppConstant.REPEAT_TIME, pintent);
    }*/

    @Override
    public void gotGpsValidationResponse(Object response, GPSErrorCode code)
    {
        if(code == GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED) {
            isGpsProviderEnabled = false;
            //showSettingsAlert();
        }
        else if(code == GPSErrorCode.EC_GPS_PROVIDER_ENABLED) {
            isGpsProviderEnabled = true;
            gpsUtills.getCurrentLatLng();
        }
        else if(code == GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION) {
            currentLatLng = (LatLng) response;
            isLocationFound = false;
        }
        else if(code == GPSErrorCode.EC_LOCATION_FOUND) {
            isLocationFound = true;
            currentLatLng = (LatLng) response;
            if(!isPhotoTaken){
                takePhoto(currentLatLng);
                LogUtils.debug("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);
            }

            isPhotoTaken = true;
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID) {
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD) {
        }
    }

    private void takePhoto(LatLng currentLatLng){
        if(camera == null)
            camera = new CameraController(CameraService.this,FOLDER_PATH);
        if(camera.hasCamera()){
            camera.getCameraInstance();
            camera.setLocation(currentLatLng.latitude,currentLatLng.longitude);
            camera.takePicture();
        }
        gpsUtills.stopLocationUpdates();
        //setupAlarm();
        stopSelf();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        gpsUtills.disConnectGoogleApiClient();
    }
}
