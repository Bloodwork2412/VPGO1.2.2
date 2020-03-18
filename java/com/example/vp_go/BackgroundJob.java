/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Algorithmus für Benachrichtigungen, welcher ca. alle 30 Minuten ausgeführt wird

package com.example.vp_go;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import static com.example.vp_go.MainActivity.URLvp;
import static com.example.vp_go.MainActivity.USERNAME;
import static com.example.vp_go.MainActivity.pass;
import static com.example.vp_go.MainActivity.user;
import static com.example.vp_go.Notifications.REPEATED_ID;

public class BackgroundJob extends JobService {

    private static final String Tag = "BackgroundJobService";
    private NotificationManagerCompat notificationManager;

    public static String SAVE = "save";
    public static final String saved = null;
    private ArrayList<Datapoint> listSave;
    private ArrayList<Datapoint> listCopy;
    private ArrayList<Datapoint> listPrint;

    private String[] news;
    private String[] currentVps;
    private String[] days;
    private String[] kurse = new String[14];
    private int downloaded;
    private String stufe;
    private String nutzername;
    private String passwort;
    private boolean unterstufe;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(Tag, "Job Scheduled");
        notificationManager = NotificationManagerCompat.from(this);
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters params){
        final JobParameters p = params;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(Tag, "starting background");
                    checkOnline();
                    jobFinished(p, false);
                }
            }).start();
        } catch (Exception ec){
            jobFinished(p, true);
        }
    }

    public void checkOnline(){
        listSave = new ArrayList<>();
        listCopy = new ArrayList<>();
        listPrint = new ArrayList<>();
        news = new String[10];
        currentVps = new String[10];
        days = new String[10];
        copyData();
        getVps();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        stufe = prefs.getString(klasse, "---");
        downloaded = 9;
        unterstufe = (!(stufe.equals("EF") || stufe.equals("Q1") || stufe.equals("Q2")));

        while(downloaded >= 0){
            if(currentVps[downloaded] != null) {
                Log.d(Tag, "downloadData " + downloaded);
                downloadFilteredData();
            }
            downloaded--;
        }
        Log.d(Tag, "saveList");
        saveList();
        Log.d(Tag, "compareData");
        compareData();
        fillListPrint();
        Log.d(Tag, "sendMessages");
        sendMessages(false, null, 0);
        Log.d(Tag, "test");
        // test();
        Log.d(Tag, "finished");
    }

    public void copyData(){
        SharedPreferences prefs = getSharedPreferences(SAVE, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(saved, null);
        if(set != null && !set.isEmpty()) {
            Object[] array = set.toArray();
            for (int i = 0; i < array.length; i++) {
                String s = (String) array[i];
                String[] parts = s.split(",_,");
                Datapoint d = new Datapoint(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                listCopy.add(d);
            }
            for(int i = 0; i < listCopy.size(); i++){
                Log.d(Tag, listCopy.get(i).getStunde());
            }
        }
    }

    public void getVps(){
        try {
            SharedPreferences sharedPreferences1 = getSharedPreferences(USERNAME, MODE_PRIVATE);
            SharedPreferences sharedPreferences2 = getSharedPreferences(PASSWORD, MODE_PRIVATE);
            nutzername = sharedPreferences1.getString(user, "");
            passwort = sharedPreferences2.getString(pass, "");

            URL obj = new URL(URLvp);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            // Öffnen der html einer https mit "basic authentication"
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

            // BufferedReader scannt die html Zeile für Zeile
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine = in.readLine();

            // überspringt den Anfang des Planes bis zu dem Quellcode der großen Buttons
            while (!inputLine.contains("Aktuelle Vertretungspläne")) {
                inputLine = in.readLine();
            }
            int slot = 0;
            boolean found = false;

            // Sucht Zeilen, die URLs beinhalten
            // Wird nur bis zu einer bestimmten Stelle in der html durchgeführt
            while (!inputLine.contains("Powered")) {
                if (inputLine.contains("/god/") && !inputLine.contains("card")) {
                    String date = inputLine.substring(77, 87);
                    inputLine = in.readLine();
                    inputLine = in.readLine();
                    int length = inputLine.length();
                    if (!inputLine.substring(length - 7, length - 5).contains("v")) {
                        length--;
                    }
                    String day = inputLine.substring(8, length - 14);

                    // Speichert das Datum und den Wochentag von bis zu 10 Plänen. Text wird auf den Buttons zum navigieren benutzt
                    // Datum wird in originalem Format benötigt, um auf die Seite eines bestimmten Tages zugreifen zu können
                    while (slot < 10 && !found) {
                        if (currentVps[slot] == null) {
                            currentVps[slot] = date;
                            days[slot] = day;
                            found = true;
                        }
                        slot++;
                    }
                    found = false;
                }
                inputLine = in.readLine();
            }
            in.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void downloadFilteredData(){
        try {
            java.net.URL obj = new URL(URLvp + "/" + currentVps[downloaded]);
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
                        //System.out.println(downloaded + " " + art + stunde + fach + vertreter + raum + notiz);
                        Datapoint point = new Datapoint(art, stunde, fach, vertreter, currentVps[downloaded], raum, notiz, days[downloaded]);
                        Log.d(Tag, "date: " + currentVps[downloaded]);
                        listSave.add(point);
                    }
                }
                inputLine= in.readLine();
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void saveList(){
        Set<String> saveSet = new HashSet<>();
        for(int i = 0; i < listSave.size(); i++){
            Datapoint d = listSave.get(i);
            String s = d.getArt() + ",_," + d.getStunde() + ",_," + d.getFach() + ",_," + d.getLehrer() + ",_," + d.getKlasse() + ",_," + d.getRaum() + ",_," + d.getNotiz() + ",_," + d.getTag();
            saveSet.add(s);
        }
        SharedPreferences prefs = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(saved, saveSet).apply();
    }

    public boolean kursFound(String kurs){
        boolean found = false;
        int i = 0;
        importSharedPreferences();
        while(!found && i < kurse.length){
            if(kurse[i].equals(kurs)){
                found = true;
            }
            i++;
        }
        return found;
    }

    public void importSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences(PREFS1, MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences(PREFS2, MODE_PRIVATE);
        SharedPreferences sharedPreferences3 = getSharedPreferences(PREFS3, MODE_PRIVATE);
        SharedPreferences sharedPreferences4 = getSharedPreferences(PREFS4, MODE_PRIVATE);
        SharedPreferences sharedPreferences5 = getSharedPreferences(PREFS5, MODE_PRIVATE);
        SharedPreferences sharedPreferences6 = getSharedPreferences(PREFS6, MODE_PRIVATE);
        SharedPreferences sharedPreferences7 = getSharedPreferences(PREFS7, MODE_PRIVATE);
        SharedPreferences sharedPreferences8 = getSharedPreferences(PREFS8, MODE_PRIVATE);
        SharedPreferences sharedPreferences9 = getSharedPreferences(PREFS9, MODE_PRIVATE);
        SharedPreferences sharedPreferences10 = getSharedPreferences(PREFS10, MODE_PRIVATE);
        SharedPreferences sharedPreferences11 = getSharedPreferences(PREFS11, MODE_PRIVATE);
        SharedPreferences sharedPreferences12 = getSharedPreferences(PREFS12, MODE_PRIVATE);
        SharedPreferences sharedPreferences13 = getSharedPreferences(PREFS13, MODE_PRIVATE);
        SharedPreferences sharedPreferences14 = getSharedPreferences(PREFS14, MODE_PRIVATE);

        stufe = sharedPreferences.getString(klasse, "---");
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

    public void compareData(){
        int n = 0;
        String lastDayFound = "";
        for (int i = 0; i < listSave.size(); i++) {
            boolean found = false;
            int x = 0;
            while (!found && x < listCopy.size()) {
                Datapoint a = listCopy.get(x);
                Datapoint b = listSave.get(i);
                if (a.getArt().equals(b.getArt()) && a.getStunde().equals(b.getStunde()) && a.getFach().equals(b.getFach()) && a.getLehrer().equals(b.getLehrer()) && a.getRaum().equals(b.getRaum()) && a.getNotiz().equals(b.getNotiz()) && a.getTag().equals(b.getTag())) {
                    found = true;
                    Log.d(Tag, "match " + a.getTag() + a.getStunde());
                }
                x++;
            }
            if (!found && !lastDayFound.equals(listSave.get(i).getKlasse())) {
                news[n] = listSave.get(i).getKlasse();
                lastDayFound = listSave.get(i).getKlasse();
                n++;
            }
        }
    }

    public void fillListPrint(){
        int i = 0;
        int n = 0;
        while(i < news.length){
            if(n < listSave.size()){
                while(n < listSave.size() && !listSave.get(n).getKlasse().equals(news[i])){
                    n++;
                }
                while(n < listSave.size() && listSave.get(n).getKlasse().equals(news[i])){
                    listPrint.add(listSave.get(n));
                    n++;
                }
            }
            i++;
        }
    }

    public void sendMessages(boolean test, String source, int n){
        String day = "";
        String s = "";
        Datapoint d = null;
        int num = 1;
        for(int i = 0; i < listPrint.size(); i++){
            d = listPrint.get(i);
            if(!s.equals("") && !day.equals(d.getTag()) && !test){
                Log.d(Tag, day);
                if(n != 0){
                    num = n;
                }
                Log.d(Tag, num + " printing " + day + " " + d.getStunde());
                sendOnChannel1(num, s.substring(1), day);
                s = "";
            }
            day = d.getTag();
            s = s + "\n" + d.getArt() + " | " + d.getStunde() + ". Stunde | " + d.getFach();
            if(!d.getLehrer().equals("-")){
                s = s + " | " + d.getLehrer();
            }
            if(!d.getRaum().equals("-")){
                s = s + " | " + d.getRaum();
            }
            if(!d.getNotiz().equals("---")){
                s = s + " | " + d.getNotiz();
            }
            num = Integer.parseInt(d.getKlasse().substring(8,10));
        }
        if(source != null) {
            day = source;
        }
        if(d != null) {
            if(n != 0){
                num = n;
            }
            Log.d(Tag, num + " printing " + day + " " + d.getStunde());
            sendOnChannel1(num, s.substring(1), day);
        }
    }

    public void test(){
        listPrint = new ArrayList<>();
        listPrint.addAll(listSave);
        sendMessages(true, "save", 11);
        listPrint = new ArrayList<>();
        listPrint.addAll(listCopy);
        sendMessages(true, "copy", 12);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public void sendOnChannel1(int n, String s, String day){
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, REPEATED_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(day)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(intent)
                .build();
        notificationManager.notify(n, notification);
    }
}
