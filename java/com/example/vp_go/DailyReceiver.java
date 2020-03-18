/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Hilfsklasse: Da zu bestimmter Uhrzeit von BootService nicht unbedingt eine Verbindung besteht,
// wird in dieser Klasse eine verschiebbare Aktualisierung geplant

package com.example.vp_go;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.example.vp_go.KurseAendernPage.dailyJobId;

public class DailyReceiver extends BroadcastReceiver {
    String Tag = "DailyScheduler";

    @Override
    public void onReceive(Context context, Intent i) {
        Log.d(Tag, "receivedJob");
        ComponentName comp = new ComponentName(context, DailyJob.class);
        JobInfo info = new JobInfo.Builder(dailyJobId, comp)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        int success = scheduler.schedule(info);
        if(success == JobScheduler.RESULT_SUCCESS){
            Log.d(Tag, "Job scheduled");
        } else {
            Log.d(Tag, "Job scheduling failed");
        }
    }
}
