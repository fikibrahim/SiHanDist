package com.haerul.sihandist.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.haerul.sihandist.ui.MainActivity;


public class AppBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("LOG_SERVICE_BROADCAST", "onReceive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent serviceIntent = new Intent(context, AppService.class);
            serviceIntent.setAction(MainActivity.STARTFOREGROUND_ACTION);
            context.startForegroundService(serviceIntent);
            Log.i(String.valueOf(this), "Service run in foreground");
        } else {
            context.startService(new Intent(context, AppService.class));
            Log.i(String.valueOf(this), "Service run in background");

        }
    }
}


