package com.example.arpaul.rideit.Receiver;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.arpaul.rideit.CameraService;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class MyWakefulReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*PowerManager.WakeLock.acquire(context);
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent service = new Intent(context, CameraService.class);
        startWakefulService(context, service);*/

        context.startService(new Intent(context, CameraService.class));
    }
}
