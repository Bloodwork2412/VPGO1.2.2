/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Startet jeden Tag zu bestimmter Uhrzeit den DailyReceiver, damit dieser im Hintergrund aktualisieren kann

package com.example.vp_go;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.util.Calendar;

import static com.example.vp_go.KurseAendernPage.TIME;
import static com.example.vp_go.KurseAendernPage.dailyTime;

public class BootService extends Service {

        public BootService() {

        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            startDailyAlarm();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return START_NOT_STICKY;
        }

        private void startDailyAlarm() {
            SharedPreferences sharedPrefs = getSharedPreferences(TIME, MODE_PRIVATE);
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            String[] time = sharedPrefs.getString(dailyTime, "off").split(":");
            if(!time[0].equals("off")) {
                Calendar calendar = Calendar.getInstance();
                if(time[0].length() == 1){
                    time[0] = "0" + time[0];
                }
                if(time[1].length() == 1){
                    time[1] = "0" + time[1];
                }
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

                Intent myIntent = new Intent(this, DailyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
