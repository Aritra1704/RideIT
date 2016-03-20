package com.example.arpaul.rideit.Utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Environment;
import android.util.SparseArray;

import com.example.arpaul.rideit.Common.AppConstant;
import com.example.arpaul.rideit.R;
import com.example.arpaul.rideit.camera.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CameraController {
    private Context context;

    private boolean hasCamera;

    private CameraSource camera;
    private int cameraId;
    private double latitude, longitude;
    private String timeStamp;
    private CameraSource.PictureCallback mPicture;

    public CameraController(Context c,CameraSource.PictureCallback mPicture){
        context = c.getApplicationContext();
        this.mPicture = mPicture;

        /*if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            cameraId = getFrontCameraId();

            if(cameraId != -1){
                hasCamera = true;
            }else{
                hasCamera = false;
            }
        }else{
            hasCamera = false;
        }*/
    }

    public boolean hasCamera(){
        return hasCamera;
    }

    public void getCameraInstance(){
        camera = null;

        if(hasCamera){
            try{
                FaceDetector facedetector = new FaceDetector.Builder(context).build();
                CameraSource.Builder builder = new CameraSource.Builder(context, facedetector)
                        .setFacing(CameraSource.CAMERA_FACING_FRONT)
                        .setRequestedFps(15.0f);
            }
            catch(Exception e){
                hasCamera = false;
            }
        }
    }

    public void takePicture(){
        if(hasCamera){
            System.gc();
            camera.takePicture(null,mPicture);
        }
    }

    public void releaseCamera(){
        if(camera != null){
            camera.release();
            camera = null;
        }
    }

    public File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), AppConstant.FOLDER_PATH);

        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        timeStamp = CalendarUtils.getCurrentDateTime();

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath()+ File.separator+"IMG_"+timeStamp+".png");

        return mediaFile;
    }

    public void setLocation(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLocationonPhoto(File pictureFile){
        OutputStream outStream = null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            Bitmap myBitmap = BitmapUtils.decodeSampledBitmapFromResource(pictureFile, 480,600);

            Paint myRectPaint = new Paint();
            myRectPaint.setStrokeWidth(5);
            myRectPaint.setColor(Color.RED);
            myRectPaint.setStyle(Paint.Style.STROKE);

            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, 0, 0, null);

            FaceDetector faceDetector = new FaceDetector.Builder(context).setTrackingEnabled(false).build();
            if(!faceDetector.isOperational()){

                return;
            }

            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Face> faces = faceDetector.detect(frame);

            for(int i=0; i<faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();
                tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
            }

            Bitmap bitmapProcessed = getBitMap(myBitmap,CalendarUtils.getCurrentDateTime());

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
