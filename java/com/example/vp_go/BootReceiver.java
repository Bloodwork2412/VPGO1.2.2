/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Wenn das Gerät neu gestartet wird, müssen Hintergrundaktivitäten neu gestartet werden

package com.example.vp_go;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BootService.class);
        context.startService(i);
    }

}