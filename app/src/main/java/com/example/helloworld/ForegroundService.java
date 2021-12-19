package com.example.helloworld;
/**
 * https://androidwave.com/foreground-service-android-example/
 * Ganesh
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.core.app.NotificationCompat;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.helloworld.MainActivity.context;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                //.setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        //stopSelf();
        System.out.println("VASA APP------------------Foreground service");
        backgroundHandler = new Handler(); // new handler
        backgroundHandler.postDelayed(runnable, 10000); // 10 mins int.
       // backgroundHandler = new Handler(handlerThread.getLooper());

       // backgroundHandler.postDelayed(runnable, 10000); // 10 mins int.

        return START_STICKY;
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            /* my set of codes for repeated work */
            MainActivity.updateUI();
            backgroundHandler.postDelayed(this, 10000); // reschedule the handler
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}