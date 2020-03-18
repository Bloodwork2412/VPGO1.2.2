/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Algorithmus für Benachrichtigungen, welcher täglich zu bestimmter Uhrzeit ausgeführt wird

package com.example.vp_go;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static com.example.vp_go.KurseAendernPage.PREFS;
import static com.example.vp_go.KurseAendernPage.PREFS1;
import static com.example.vp_go.KurseAendernPage.PREFS10;
import static com.example.vp_go.KurseAendernPage.PREFS11;
import static com.example.vp_go.KurseAendernPage.PREFS12;
import static com.example.vp_go.KurseAendernPage.PREFS13;
import static com.example.vp_go.KurseAendernPage.PREFS14;
import static com.example.vp_go.KurseAendernPage.PREFS2;
import static com.example.vp_go.KurseAendernPage.PREFS3;
import static com.example.vp_go.KurseAendernPage.PREFS4;
import static com.example.vp_go.KurseAendernPage.PREFS5;
import static com.example.vp_go.KurseAendernPage.PREFS6;
import static com.example.vp_go.KurseAendernPage.PREFS7;
import static com.example.vp_go.KurseAendernPage.PREFS8;
import static com.example.vp_go.KurseAendernPage.PREFS9;
import static com.example.vp_go.KurseAendernPage.dailyJobId;
import static com.example.vp_go.KurseAendernPage.klasse;
import static com.example.vp_go.KurseAendernPage.kurs1;
import static com.example.vp_go.KurseAendernPage.kurs10;
import static com.example.vp_go.KurseAendernPage.kurs11;
import static com.example.vp_go.KurseAendernPage.kurs12;
import static com.example.vp_go.KurseAendernPage.kurs13;
import static com.example.vp_go.KurseAendernPage.kurs14;
import static com.example.vp_go.KurseAendernPage.kurs2;
import static com.example.vp_go.KurseAendernPage.kurs3;
import static com.example.vp_go.KurseAendernPage.kurs4;
import static com.example.vp_go.KurseAendernPage.kurs5;
import static com.example.vp_go.KurseAendernPage.kurs6;
import static com.example.vp_go.KurseAendernPage.kurs7;
import static com.example.vp_go.KurseAendernPage.kurs8;
import static com.example.vp_go.KurseAendernPage.kurs9;
import static com.example.vp_go.MainActivity.PASSWORD;
import static com.example.vp_go.MainActivity.USERNAME;
import static com.example.vp_go.MainActivity.pass;
import static com.example.vp_go.MainActivity.user;
import static com.example.vp_go.MainActivity.URLvp;
import static com.example.vp_go.Notifications.DAILY_ID;

public class DailyJob extends JobService {

    private String[] kurse = new String[14];
    private Context context;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        context = this;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                importSharedPreferences();
                getCurrentData(jobParameters);
            }
        });
        thread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public void getCurrentData(JobParameters p){
        String s = "";

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            String stufe = prefs.getString(klasse, "---");
            SharedPreferences sharedPreferences1 = context.getSharedPreferences(USERNAME, MODE_PRIVATE);
            SharedPreferences sharedPreferences2 = context.getSharedPreferences(PASSWORD, MODE_PRIVATE);
            String nutzername = sharedPreferences1.getString(user, "");
            String passwort = sharedPreferences2.getString(pass, "");
            boolean unterstufe = (!(stufe.equals("EF") || stufe.equals("Q1") || stufe.equals("Q2")));

            String dateVP = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            System.out.println(dateVP);
            // dateVP = "2020-03-02";
            java.net.URL obj = new URL(URLvp + "/" + dateVP);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            // Passwort der https Seite wird eingegeben
            String userCredentials = nutzername + ":" + passwort;
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:27.0) Gecko/20100101 Firefox/27.0.2 Waterfox/27.0");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String urlParameters = "param1=value1&param2=value2";

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine = in.readLine();

            while(!inputLine.contains("Version")){
                inputLine = in.readLine();
            }

            while (!inputLine.contains("Es wurde ein Filter auf")) {
                //System.out.println("empty " + inputLine);
                if (inputLine.contains("index") && inputLine.contains(stufe)) {

                    String art;
                    String stunde;
                    String fach;
                    String vertreter;
                    String raum;
                    String notiz;

                    // Speichern der verschiedenen Daten erfolg in immer gleicher Reihenfolge
                    inputLine = in.readLine();
                    int lengthArt = (inputLine).length();
                    art = (inputLine).substring(38, lengthArt - 5);

                    inputLine = in.readLine();
                    int lengthStunde = (inputLine).length();
                    stunde = (inputLine).substring(38, lengthStunde - 5);

                    inputLine = in.readLine();
                    int lengthFach = (inputLine).length();
                    fach = (inputLine).substring(41, lengthFach - 5);
                    if (fach.contains("---")) {
                        fach = "-";
                    }

                    // evA und Entfall wird statt zu "Vertreter" zu "Art" geschrieben
                    inputLine = in.readLine();
                    int lengthVertreter = (inputLine).length();
                    vertreter = (inputLine).substring(49, lengthVertreter - 5);
                    if ((inputLine).contains("evA") || (inputLine).contains("Entfall")) {
                        art = vertreter;
                        vertreter = "-";
                    }

                    inputLine = in.readLine();
                    inputLine = in.readLine();
                    int lengthRaum = (inputLine).length();
                    // Manchmal wird kein Raum angegeben
                    if ((inputLine).contains("span")) {
                        raum = "-";
                    } else {
                        raum = (inputLine).substring(48, lengthRaum - 5);
                    }

                    inputLine = in.readLine();
                    inputLine = in.readLine();

                    // Notiz vom Lehrer, die gegebenenfalls in einem Popup angezeigt wird
                    if (!(inputLine).contains("text-muted")) {
                        int lengthNotiz = (inputLine).length();
                        notiz = (inputLine).substring(50, lengthNotiz - 5);
                        // Anführungszeichen wurden nicht korrekt angezeigt
                        notiz = notiz.replaceAll("&quot;", "\"");
                    } else {
                        notiz = "---";
                    }

                    if(unterstufe || kursFound(fach)) {
                        s = s + "\n" + art + " | " + stunde + ". Stunde | " + fach;
                        if(!vertreter.equals("-")){
                            s = s + " | " + vertreter;
                        }
                        if(!raum.equals("-")){
                            s = s + " | " + raum;
                        }
                        if(!notiz.equals("---")){
                            s = s + " | " + notiz;
                        }
                    }
                }
                inputLine= in.readLine();
            }

        } catch (Exception ex){
            ex.printStackTrace();
            s = "error";
        }

        if(!s.equals("error")) {
            if (s.equals("")) {
                s = "xHeute entfallen keine Stunden";
            }
            sendOnChannel2(s.substring(1));
            jobFinished(p, false);
            System.out.println("cancel DailyJob");
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.cancel(dailyJobId);
        } else {
            System.out.println("reschedule DailyJob");
            jobFinished(p, true);
        }
    }

    public boolean kursFound(String kurs){
        boolean found = false;
        int i = 0;
        while(!found && i < kurse.length){
            if(kurse[i].equals(kurs)){
                found = true;
            }
            i++;
        }
        return found;
    }

    public void importSharedPreferences() {
        SharedPreferences sharedPreferences1 = context.getSharedPreferences(PREFS1, MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = context.getSharedPreferences(PREFS2, MODE_PRIVATE);
        SharedPreferences sharedPreferences3 = context.getSharedPreferences(PREFS3, MODE_PRIVATE);
        SharedPreferences sharedPreferences4 = context.getSharedPreferences(PREFS4, MODE_PRIVATE);
        SharedPreferences sharedPreferences5 = context.getSharedPreferences(PREFS5, MODE_PRIVATE);
        SharedPreferences sharedPreferences6 = context.getSharedPreferences(PREFS6, MODE_PRIVATE);
        SharedPreferences sharedPreferences7 = context.getSharedPreferences(PREFS7, MODE_PRIVATE);
        SharedPreferences sharedPreferences8 = context.getSharedPreferences(PREFS8, MODE_PRIVATE);
        SharedPreferences sharedPreferences9 = context.getSharedPreferences(PREFS9, MODE_PRIVATE);
        SharedPreferences sharedPreferences10 = context.getSharedPreferences(PREFS10, MODE_PRIVATE);
        SharedPreferences sharedPreferences11 = context.getSharedPreferences(PREFS11, MODE_PRIVATE);
        SharedPreferences sharedPreferences12 = context.getSharedPreferences(PREFS12, MODE_PRIVATE);
        SharedPreferences sharedPreferences13 = context.getSharedPreferences(PREFS13, MODE_PRIVATE);
        SharedPreferences sharedPreferences14 = context.getSharedPreferences(PREFS14, MODE_PRIVATE);

        kurse[0] = sharedPreferences1.getString(kurs1, "---");
        kurse[1] = sharedPreferences2.getString(kurs2, "---");
        kurse[2] = sharedPreferences3.getString(kurs3, "---");
        kurse[3] = sharedPreferences4.getString(kurs4, "---");
        kurse[4] = sharedPreferences5.getString(kurs5, "---");
        kurse[5] = sharedPreferences6.getString(kurs6, "---");
        kurse[6] = sharedPreferences7.getString(kurs7, "---");
        kurse[7] = sharedPreferences8.getString(kurs8, "---");
        kurse[8] = sharedPreferences9.getString(kurs9, "---");
        kurse[9] = sharedPreferences10.getString(kurs10, "---");
        kurse[10] = sharedPreferences11.getString(kurs11, "---");
        kurse[11] = sharedPreferences12.getString(kurs12, "---");
        kurse[12] = sharedPreferences13.getString(kurs13, "---");
        kurse[13] = sharedPreferences14.getString(kurs14, "---");
    }

    public void sendOnChannel2(String s){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DAILY_ID);
        PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Heute")
                .setContentIntent(intent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s));

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }
}
