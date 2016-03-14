package com.example.arpaul.rideit.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CameraController {
    private Context context;

    private boolean hasCamera;

    private Camera camera;
    private int cameraId;
    private String folderpath;
    private double latitude, longitude;
    private String timeStamp;

    public CameraController(Context c,String folderpath){
        context = c.getApplicationContext();
        this.folderpath = folderpath;

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            cameraId = getFrontCameraId();

            if(cameraId != -1){
                hasCamera = true;
            }else{
                hasCamera = false;
            }
        }else{
            hasCamera = false;
        }
    }

    public boolean hasCamera(){
        return hasCamera;
    }

    public void getCameraInstance(){
        camera = null;

        if(hasCamera){
            try{
                camera = Camera.open(cameraId);
                prepareCamera();
            }
            catch(Exception e){
                hasCamera = false;
            }
        }
    }

    public void takePicture(){
        if(hasCamera){
            camera.takePicture(null,null,mPicture);
        }
    }

    public void releaseCamera(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private int getFrontCameraId(){
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i = 0;i < numberOfCameras;i++){
            Camera.getCameraInfo(i,ci);
            if(ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                camId = i;
            }
        }

        return camId;
    }

    private void prepareCamera(){
        SurfaceView view = new SurfaceView(context);

        try{
            camera.setPreviewDisplay(view.getHolder());
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        camera.startPreview();

        Camera.Parameters params = camera.getParameters();
        params.setJpegQuality(100);

        camera.setParameters(params);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            File pictureFile = getOutputMediaFile();

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

            setLocationonPhoto(pictureFile);
            releaseCamera();
        }
    };

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),folderpath);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        timeStamp = CalendarUtils.getCurrentDateTime();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath()+ File.separator+"IMG_"+timeStamp+".png");

        return mediaFile;
    }

    public void setLocation(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void setLocationonPhoto(File pictureFile){
        OutputStream outStream = null;
        try {
            Bitmap bmp = BitmapUtils.decodeSampledBitmapFromResource(pictureFile, 480,600);
            Bitmap bitmapProcessed = getBitMap(bmp,CalendarUtils.getCurrentDateTime());

            if (pictureFile.exists ())
                pictureFile.delete ();
            try
            {
                FileOutputStream out = new FileOutputStream(pictureFile);
                bitmapProcessed.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitMap(Bitmap bmp,String date)
    {
        Bitmap mBtBitmap = null;
        if(bmp != null)
        {
            mBtBitmap = BitmapUtils.processBitmap(bmp, date,latitude,longitude);
            if(bmp!=null && !bmp.isRecycled())
                bmp.recycle();
            return mBtBitmap;
        }
        return mBtBitmap;
    }
}
