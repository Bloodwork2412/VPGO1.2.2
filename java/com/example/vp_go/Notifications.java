/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Benachrichtigungskanäle werden verwaltet

package com.example.vp_go;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {

    public static final String REPEATED_ID = "channelRepeated";
    public static final String DAILY_ID = "channelDaily";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotifications();
    }

    public void createNotifications(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(REPEATED_ID, "Intervall Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Sendet Nachricht falls neues Ereignis gefunden wurde");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            NotificationChannel channel2 = new NotificationChannel(DAILY_ID, "Daily Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Sendet täglich eine Nachricht");
            manager.createNotificationChannel(channel2);
        }
    }
}
