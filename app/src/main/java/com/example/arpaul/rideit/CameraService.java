package com.example.arpaul.rideit;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.example.arpaul.rideit.Common.AppPreference;
import com.example.arpaul.rideit.GPSUtilities.GPSCallback;
import com.example.arpaul.rideit.GPSUtilities.GPSErrorCode;
import com.example.arpaul.rideit.GPSUtilities.GPSUtills;
import com.example.arpaul.rideit.Utilities.CalendarUtils;
import com.example.arpaul.rideit.Utilities.CameraController;
import com.example.arpaul.rideit.Utilities.LogUtils;
import com.example.arpaul.rideit.camera.CameraSource;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CameraService extends Service implements GPSCallback, CameraSource.PictureCallback {

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String lastPhoto = new AppPreference(CameraService.this).getStringFromPreference(AppPreference.IS_STARTED,"");
        if(CalendarUtils.getDiffBtwDatesInMinutes(lastPhoto,CalendarUtils.getCurrentDateTime()) < 5)
            stopSelf();
        //Gps
        gpsUtills = GPSUtills.getInstance(CameraService.this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());

        gpsUtills.setListner(CameraService.this);
        gpsUtills.isGpsProviderEnabled();
        gpsUtills.connectGoogleApiClient();
        gpsUtills.startLocationUpdates();
        getCurrentLocation();

        camera = new CameraController(CameraService.this,CameraService.this);

        //to read the logcat programmatically
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                log.append(line);
            }

            //to create a Text file name "logcat.txt" in SDCard
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/myLogcat");
            dir.mkdirs();
            File file = new File(dir, "logcat.txt");

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(log.toString());
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Service.START_STICKY;
    }

    private void getCurrentLocation(){
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                gpsUtills.getCurrentLatLng();
            }
        }, 2 * 1000);
    }

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
            camera = new CameraController(CameraService.this,CameraService.this);
        if(camera.hasCamera()){
            camera.getCameraInstance();
            camera.setLocation(currentLatLng.latitude,currentLatLng.longitude);
            camera.takePicture();
        }
        gpsUtills.stopLocationUpdates();
        //setupAlarm();
        new AppPreference(CameraService.this).saveStringInPreference(AppPreference.LAST_PHOTO , CalendarUtils.getCurrentDateTime());
        stopSelf();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        gpsUtills.disConnectGoogleApiClient();
    }

    @Override
    public void onPictureTaken(byte[] data) {
        File pictureFile = camera.getOutputMediaFile();

        if(pictureFile == null){
            LogUtils.debug("TEST", "Error creating media file, check storage permissions");
            return;
        }

        try{
            LogUtils.debug("TEST","File created");
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();

        }catch(FileNotFoundException e){
            LogUtils.debug("TEST","File not found: "+e.getMessage());
        } catch (IOException e){
            LogUtils.debug("TEST","Error accessing file: "+e.getMessage());
        }

        camera.setLocationonPhoto(pictureFile);
        camera.releaseCamera();
    }
}
