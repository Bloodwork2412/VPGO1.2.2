
/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Klasse der Startseite der App

package com.example.vp_go;

// Imports für Analysesoftware
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import androidx.annotation.ColorInt; // Farbreferenzen können geladen werden
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

// Für Benachrichtigungen
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// Verschlüsselung der Anmeldedaten
import org.apache.commons.codec.binary.Base64;

// Für Zugriff auf https
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Calendar; // Für Benachrichtigungen

import javax.net.ssl.HttpsURLConnection;

// Für gespeicherte Daten
import static com.example.vp_go.KurseAendernPage.DARKMODE;
import static com.example.vp_go.KurseAendernPage.NETWORK;
import static com.example.vp_go.KurseAendernPage.TIME;
import static com.example.vp_go.KurseAendernPage.PREFS;
import static com.example.vp_go.KurseAendernPage.PREFS1;
import static com.example.vp_go.KurseAendernPage.PREFS2;
import static com.example.vp_go.KurseAendernPage.PREFS3;
import static com.example.vp_go.KurseAendernPage.PREFS4;
import static com.example.vp_go.KurseAendernPage.PREFS5;
import static com.example.vp_go.KurseAendernPage.PREFS6;
import static com.example.vp_go.KurseAendernPage.PREFS7;
import static com.example.vp_go.KurseAendernPage.PREFS8;
import static com.example.vp_go.KurseAendernPage.PREFS9;
import static com.example.vp_go.KurseAendernPage.PREFS10;
import static com.example.vp_go.KurseAendernPage.PREFS11;
import static com.example.vp_go.KurseAendernPage.PREFS12;
import static com.example.vp_go.KurseAendernPage.PREFS13;
import static com.example.vp_go.KurseAendernPage.PREFS14;
import static com.example.vp_go.KurseAendernPage.TEST;
import static com.example.vp_go.KurseAendernPage.dailyJobId;
import static com.example.vp_go.KurseAendernPage.darkMode;
import static com.example.vp_go.KurseAendernPage.netMode;
import static com.example.vp_go.KurseAendernPage.dailyTime;
import static com.example.vp_go.KurseAendernPage.klasse;
import static com.example.vp_go.KurseAendernPage.kurs1;
import static com.example.vp_go.KurseAendernPage.kurs2;
import static com.example.vp_go.KurseAendernPage.kurs3;
import static com.example.vp_go.KurseAendernPage.kurs4;
import static com.example.vp_go.KurseAendernPage.kurs5;
import static com.example.vp_go.KurseAendernPage.kurs6;
import static com.example.vp_go.KurseAendernPage.kurs7;
import static com.example.vp_go.KurseAendernPage.kurs8;
import static com.example.vp_go.KurseAendernPage.kurs9;
import static com.example.vp_go.KurseAendernPage.kurs10;
import static com.example.vp_go.KurseAendernPage.kurs11;
import static com.example.vp_go.KurseAendernPage.kurs12;
import static com.example.vp_go.KurseAendernPage.kurs13;
import static com.example.vp_go.KurseAendernPage.kurs14;
import static com.example.vp_go.KurseAendernPage.testMode;
import static com.example.vp_go.KurseAendernPage.repeatedJobId;


public class MainActivity extends AppCompatActivity {

    // Anmeldedaten werden dauerhaft gespeichert
    // Daten die beim ersten Öffnen genutzt werden
    public static final String URLvp = "https://vp.gymnasium-odenthal.de/god";
    public static String USERNAME = "sharedUsername";
    public static String PASSWORD = "sharedPassword";
    public static final String user = "";
    public static final String pass = "";
    public static String PREFS0 = "intro"; // Default ist, dass die App noch nicht geöffnet wurde
    public static final String firstLaunch = "true"; // Überprüfen, ob die App zum allerersten Mal gestartet wird

    // Bestandteile der Tabelle zur Anzeige von Ereignissen
    private TextView p1Art, p1Stunde, p1Fach, p1Vertreter, p1Klasse, p1Raum, p1Notiz;
    private TextView p1Nachrichten, p1Tag, p1Version, p1KeinEntfall, p1TagDavorText, p1TagDanachText, p1TagBottom;
    private TableLayout tableArt, tableStunde, tableFach, tableVertreter, tableKlasse, tableRaum;
    private NestedScrollView p1ScrollInner;
    private ScrollView p1ScrollNachricht;
    private HorizontalScrollView p1ScrollHorizontal;
    private ConstraintLayout mainLayout;
    // Andere Bestandteile der Oberfläche
    private Button p1Mode;
    private ImageView p1NeuLaden;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;

    // Konstanten
    private static final String Tag = "MainActivity";
    private final int amountPlans = 10;

    private ArrayList<Datapoint>[] list; // Speicherung der Daten
    private View lastView; // unterste Ereignisreihe
    private String nutzername = "";
    private String passwort = "";
    private String stufe; // Stufe/Klasse
    private String entfallText; // Text falls keine Ereignisse oder keine Verbindung
    private String[] kurse = new String[14]; // Kurse importiert von der anderen Activity
    private String[] currentVps = new String[amountPlans]; // Alle aktuell öffentlichen Pläne
    private String[] days = new String[amountPlans]; // Wochentag + Datum aller aktuellen Pläne
    private int mode; // Filtermöglichkeiten 1=alles, 2=stufe, 3=eigene
    private int currentPage; // Seite auf der der Nutzer sich befindet
    private int anzahlKurse; // Anzahl der eingetragenen Kurse
    private int downloaded; // Anzahl heruntergeladener Pläne
    private int downloadedLast; // Hilfsvariable falls keine Verbindung
    private int timeCount; // Speichert vergangene Zeit für Swipe-Algorithmus
    private long lastTimeTouched; // Uhrzeit der letzten Berührung für Swipe-Algorithmus
    private boolean canRight; // Darf einen Tag nach vorne wipen
    private boolean canLeft; // Darf einen Tag nach hinten swipen
    private boolean linesFound; // Wurden Ereignisse gefunden?
    private boolean working; // true falls UI gerade aktualisiert wird
    private boolean workingShort; // Tag wird gerade gewechselt
    private boolean emptiedAll; // Alle Textfelder wurden geleert
    private boolean filledViews; // Alle Textfelder gefüllt
    private boolean newModeText; // Text bei Filterwechsel wurde gewechselt
    private boolean isCurrentVpsDone; // Überprüft, ob ein Thread fertig ist
    private boolean unterstufe; // Schüler ist nicht in Oberstufe
    private boolean error; // Fehler ist aufgetreten
    private boolean firstError; // Tritt ein Error zum ersten Mal auf, so wird alles nochmal neu geladen
    private boolean noData; // Es stehen keine Ereignisse auf dem Vertretungsplan für den Tag
    private boolean filledCurrent; // Alle UIthreads fertig
    private boolean switchesInvisible; // Dark mode Button unsichtbar
    private boolean hasData; // Es wurden Daten gefunden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dark mode oder Light mode Farben
        SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
        final boolean dark = prefs.getBoolean(darkMode, false);
        if(dark){
            setTheme(R.style.doodleDark);
        } else {
            setTheme(R.style.doodleLight);
        }
        setContentView(R.layout.intro_background);

        // Verbindung zu einer Analysesoftware für Programmierer wird hergestellt
        try {
            AppCenter.start(getApplication(), "c6e59beb-f324-475e-82c3-099e8556ac60", Analytics.class, Crashes.class);
        } catch (Exception ex) {

        }

        // Falls App zum ersten Mal geöffnet wird
        if (checkIfFirstLaunch()) {
            introPopup();
        } else {
            setContentView(R.layout.activity_main);
            mainLayout = findViewById(R.id.activityMain);
            if(mainLayout != null) {
                // Hintergrundbild
                if (dark) {
                    mainLayout.setBackgroundResource(R.drawable.background_darkmode_on);
                } else {
                    mainLayout.setBackgroundResource(R.drawable.background_darkmode_off);
                }
            }
            // Nutzername und Passwort werden geladen
            getCredentials();

            mode = 3;
            hasData = false;
            timeCount = 0;
            lastTimeTouched = System.currentTimeMillis();
            workingShort = false;
            canLeft = false;
            canRight = false;

            findViews();
            listen();
            updateAnzahlKurse();
            p1Mode.setText(modeText(mode));
            //findTables();
            firstError = true;
            reload();
        }
    }

    // Berührungen werden weitergeleitet
    public void listen() {
        // Tag wechseln mit Swipe
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight(){
                tagDavor(null);
                Log.d("SWIPE", "swiped right");
            }
            public void onSwipeLeft(){
                tagDanach(null);
                Log.d("SWIPE", "swiped left");
            }
        });

        // Berührungen der Scrollviews werden weitergeleitet
        p1ScrollInner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchedTable(event);
                return true;
            }
        });

        p1ScrollHorizontal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchedTable(event);
                return true;
            }
        });

        p1ScrollNachricht.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchedTable(event);
                return true;
            }
        });
    }

    /* Berührungen von ScrollViews werden interpretiert
     *  Dadurch kann auf der Tabelle zum nächsten Tag geswiped werden
     *  Noch in Testphase
     */
    public void touchedTable(MotionEvent event){
        timeCount++;
        System.out.println(timeCount);
        float newX = event.getAxisValue(MotionEvent.AXIS_X);
        float oldX;
        if(event.getHistorySize() == 0){
            oldX = newX;
        } else {
            oldX = event.getHistoricalAxisValue(MotionEvent.AXIS_X, event.getHistorySize() - 1);
        }
        float x0 = newX - oldX;

        float newY = event.getAxisValue(MotionEvent.AXIS_Y);
        float oldY;
        if(event.getHistorySize() == 0){
            oldY = newY;
        } else {
            oldY = event.getHistoricalAxisValue(MotionEvent.AXIS_Y, event.getHistorySize() - 1);
        }
        float y0 = newY - oldY;
        //System.out.println("                             " + y);

        long thisTimeTouched = System.currentTimeMillis();
        boolean canChange = (thisTimeTouched - lastTimeTouched > 150);
        // System.out.println(x);
        if(canChange) {
            System.out.println("Wind of change            " + (thisTimeTouched - lastTimeTouched));
        }
        lastTimeTouched = thisTimeTouched;

        if(canChange && (p1ScrollHorizontal.getScrollX() >= p1ScrollInner.getWidth() - p1ScrollHorizontal.getWidth() || !linesFound)) {
            canRight = true;
        }
        if(canChange && (p1ScrollHorizontal.getScrollX() == 0 || !linesFound)){
            canLeft = true;
        }
        if(x0 > 1 && canRight){
            // System.out.println("not right");
            canRight = false;
        }
        if(x0 < -1 && canLeft){
            // System.out.println("not left");
            canLeft = false;
        }
        float x = (float) (x0*1.5);
        float y = y0*2;

        // System.out.println("                           " + x);
        // System.out.println("      " + p1ScrollHorizontal.getScrollX());
        // System.out.println("                           " + (p1ScrollInner.getWidth() - p1ScrollHorizontal.getWidth()));

        // Tag wird gewechselt oder ScrollViews verschieben sich
        if(right(x, y) && canRight){
            System.out.println("1");
            canRight = false;
            canLeft = false;
            timeCount = 0;
            workingShort = true;
            switchTo(1);
        } else if(left(x, y) && canLeft){
            System.out.println("2");
            canRight = false;
            canLeft = false;
            timeCount = 0;
            workingShort = true;
            switchTo(0);
        } else if(y < 0){
            // System.out.println("      " + p1ScrollNachricht.getScrollY());
            // System.out.println("                                     " + p1ScrollHorizontal.getY());
            if(p1ScrollNachricht.getScrollY() < p1ScrollHorizontal.getY()){
                // System.out.println("3");
                moveT1(y);
                moveT2(x);
            } else {
                // System.out.println("4");
                moveT2(x);
                moveT3(y);
            }
        } else {
            if(p1ScrollInner.getScrollY() == 0){
                // System.out.println("5");
                moveT1(y);
                moveT2(x);
            } else {
                // System.out.println("6");
                moveT2(x);
                moveT3(y);
            }
        }
    }

    // p1ScrollNacchricht
    public void moveT1(float yF){
        int y = (int) yF * 2;
        // System.out.println("1  " + y);
        p1ScrollNachricht.smoothScrollTo(0, p1ScrollNachricht.getScrollY() - y);
    }

    // p1ScrollHorizontal
    public void moveT2(float xF){
        int x = (int) xF * 2;
        // System.out.println("2  " + x);
        p1ScrollHorizontal.smoothScrollTo(p1ScrollHorizontal.getScrollX() - x, 0);
    }

    // p1ScrollInner
    public void moveT3(float yF){
        int y = (int) yF * 2;
        // System.out.println("3  " + y);
        p1ScrollInner.smoothScrollTo(0, p1ScrollInner.getScrollY() - y);
    }

    // wird nach links geswiped?
    public boolean left(float x, float y){
        if(x > 20 && y*y < x*x){
            // System.out.println(x*x/y*y);
            if(p1ScrollHorizontal.getScrollX() == 0 || !linesFound){
                return true;
            }
        }
        return false;
    }

    // wird nach rechts geswiped?
    public boolean right(float x, float y){
        if(x < -20 && y*y < x*x){
            // System.out.println("first right  " + (p1ScrollHorizontal.getScrollX() >= p1ScrollInner.getWidth() - p1ScrollHorizontal.getWidth()));
            if(p1ScrollHorizontal.getScrollX() >= p1ScrollInner.getWidth() - p1ScrollHorizontal.getWidth() || !linesFound){
                return true;
            }
        }
        return false;
    }

    // Tag wird gewechselt
    public void switchTo(int i){
        System.out.println("pending switch");
        final int j = i;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int x = timeCount - 1;
                while(x < timeCount){
                    x = timeCount;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("            " + (x - timeCount));
                }
                System.out.println("confirmed switch");
                if(j == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            workingShort = false;
                            tagDavor(null);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            workingShort = false;
                            tagDanach(null);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public void resizeScrollables() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p1ScrollInner.setLayoutParams(layoutParams);
    }

    // Höhe der Nachrichten-ScrollView wird angepasst
    public void formeNachricht() {
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!linesFound) {
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    p1ScrollInner.setLayoutParams(layoutParams);
                } else {
                    int wrapHeight = (int) lastView.getY() + lastView.getHeight();
                    int height = (p1ScrollNachricht.getHeight() - p1Art.getMeasuredHeight());
                    if (wrapHeight < height) {
                        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, wrapHeight + pixel);
                        p1ScrollInner.setLayoutParams(layoutParams);
                    } else {
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        p1ScrollInner.setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }

    // Überprüft, ob die App zum allerersten Mal gestartet wird
    // oder keine Anmeldedaten vorliegen (Nur bei dieser Version (1.1.3-demo) nötig, da dieses System vorher anders war)
    public boolean checkIfFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(TEST, MODE_PRIVATE);
        SharedPreferences sharedPreferences0 = getSharedPreferences(PREFS0, MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences(USERNAME, MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences(PASSWORD, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(testMode, false)) {
            System.out.println("test" + sharedPreferences0.getString(firstLaunch, "true") + "-" + sharedPreferences1.getString(user, "") + "-" + sharedPreferences2.getString(user, ""));
            return (sharedPreferences0.getString(firstLaunch, "true").equals("true"));
        } else {
            System.out.println(sharedPreferences0.getString(firstLaunch, "true") + "-" + sharedPreferences1.getString(user, "") + "-" + sharedPreferences2.getString(user, ""));
            return ((sharedPreferences0.getString(firstLaunch, "true").equals("true")) || (sharedPreferences1.getString(user, "").equals("")) || (sharedPreferences2.getString(user, "").equals("")));
        }
    }

    // Anmeldedaten werden auf globale Variablen geladen
    private void getCredentials() {
        SharedPreferences sharedPreferences1 = getSharedPreferences(USERNAME, MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences(PASSWORD, MODE_PRIVATE);

        nutzername = sharedPreferences1.getString(user, "");
        passwort = sharedPreferences2.getString(pass, "");
    }

    // Je weniger Kurse, desto schneller der Suchprozess
    public void updateAnzahlKurse() {
        importSharedPreferences();
        anzahlKurse = 13;
        for (int i = 13; i >= 0; i--) {
            if (kurse[i].equals("+")) {
                anzahlKurse = i;
            }
        }
        unterstufe = (!(stufe.equals("EF") || stufe.equals("Q1") || stufe.equals("Q2")));
    }

    // Alle gespeicherten Daten (Kurse und Klasse) werden geladen
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

        stufe = sharedPreferences.getString(klasse, "+");
        kurse[0] = sharedPreferences1.getString(kurs1, "+");
        kurse[1] = sharedPreferences2.getString(kurs2, "+");
        kurse[2] = sharedPreferences3.getString(kurs3, "+");
        kurse[3] = sharedPreferences4.getString(kurs4, "+");
        kurse[4] = sharedPreferences5.getString(kurs5, "+");
        kurse[5] = sharedPreferences6.getString(kurs6, "+");
        kurse[6] = sharedPreferences7.getString(kurs7, "+");
        kurse[7] = sharedPreferences8.getString(kurs8, "+");
        kurse[8] = sharedPreferences9.getString(kurs9, "+");
        kurse[9] = sharedPreferences10.getString(kurs10, "+");
        kurse[10] = sharedPreferences11.getString(kurs11, "+");
        kurse[11] = sharedPreferences12.getString(kurs12, "+");
        kurse[12] = sharedPreferences13.getString(kurs13, "+");
        kurse[13] = sharedPreferences14.getString(kurs14, "+");
    }

    //Alle Arrays werden "geleert", damit keine alten Daten gespeichert werden
    public void emptyArrays() {
        for (int i = 0; i < amountPlans; i++) {
            currentVps[i] = "---";
        }
        for (int i = 0; i < amountPlans; i++) {
            days[i] = "---";
        }
    }

    // "Kurse Ändern" Seite/Activity wird geöffnet
    public void kurseAendern(View view) {
        Intent intent = new Intent(this, KurseAendernPage.class);
        startActivity(intent);
    }

    // Button der zum Tag davor führt, falls vorhanden
    public void tagDavor(View view) {
        if (!working && !workingShort && currentPage != 0) {
            workingShort = true;
            p1TagDavorText.setVisibility(View.INVISIBLE);
            p1TagDanachText.setVisibility(View.INVISIBLE);
            // p1NeuLaden.setVisibility(View.INVISIBLE);
            firstError = true;
            // bei reload wir die URL geladen, die im Array eine Position weiter vorne steht
            currentPage--;
            setViews();
        }
    }

    // Button der zum Tag danach führt, falls vorhanden
    public void tagDanach(View view) {
        if (!working && !workingShort && downloaded > currentPage + 1) {
            workingShort = true;
            p1TagDavorText.setVisibility(View.INVISIBLE);
            p1TagDanachText.setVisibility(View.INVISIBLE);
            // p1NeuLaden.setVisibility(View.INVISIBLE);
            firstError = true;
            // bei reload wir die URL geladen, die im Array eine Position weiter hinten steht
            currentPage++;
            setViews();
        }
    }

    // Button NeuLaden
    public void neuLaden(View view) {
        if (!working) {
            working = true;
            p1TagDavorText.setVisibility(View.INVISIBLE);
            p1TagDanachText.setVisibility(View.INVISIBLE);
            p1NeuLaden.setVisibility(View.INVISIBLE);
            p1KeinEntfall.setText("");
            firstError = true;
            //Überprüft nur, ob es neue VPs gibt, falls es bisher keine gab
            reload();
        }
    }

    // Allgemeine Methode zum Aktualisieren der Daten
    public void reload() {
        working = true;
        currentPage = 0;
        // System.out.println("downloaded Old: " + downloaded);
        if(!error) {
            downloadedLast = downloaded;
        }
        downloaded = 0;

        // Felder werden unsichtbar
        tableTopInvisible();
        emptiedAll = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                emptyTables();
                setTextFieldsEmpty();
                emptiedAll = true;
            }
        });

        // Zusätzlicher Thread wird benötigt, da es sonst zu einem Runtime Error kommt
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(TEST, MODE_PRIVATE);
                if (!sharedPreferences.getBoolean(testMode, false)) {
                    getCurrentVps();
                } else {
                    System.out.println("in test mode");
                    emptyArrays();
                    setTestVps();
                }
                if(error && !hasData){
                    emptyArrays();
                }
                if(!currentVps[0].equals("---")) {
                    System.out.println("getHtml");
                    isCurrentVpsDone = true;
                    getHtml();
                }
                filledCurrent = false;
                if(!error || hasData) {
                    setViews();
                    working = false;
                    if(!error) {
                        System.out.println("downloading All " + hasData);
                        downloadAll();
                    } else {
                        // Falls ein Fehler aufgetreten ist
                        System.out.println("toasting");
                        downloaded = downloadedLast;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "#Error: Nicht aktualisiert", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    // System.out.println("doing this");
                    entfallText = "---";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            aktualisiereKeinEntfall();
                            updateViewsVisibility();
                            updateButtons();
                            formeNachricht();
                            working = false;
                            filledCurrent = true;
                            System.out.println("ended");
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        while(!filledCurrent){
                            // Wartet bis Felder gefüllt sind
                            try {
                                Thread.sleep(200);
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        p1NeuLaden.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        thread.start();
    }

    // Textfelder wird geleert (Nutzer-Feedback)
    public void setTextFieldsEmpty() {
        p1Tag.setText("");
        p1TagBottom.setText("");
        p1Version.setText("");
        p1Nachrichten.setText("");
        p1KeinEntfall.setVisibility(View.GONE);
        System.out.println("INVISIBLE");
    }

    // Überprüft die HTML der Startseite auf alle veröffentlichten Tage/Pläne
    public void getCurrentVps() {
        error = false;
        try {
            String URL = URLvp; // angegebene URL-Adresse der Homepage
            URL obj = new URL(URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            String user_name = nutzername;
            String password = passwort;

            try {
                // Öffnen der HTML einer "Secure-HTTP" mit "basic authentication"
                // Ab hier Code von Chilly Facts
                String userCredentials = user_name + ":" + password; // Zuvor gespeicherte Anmeldedaten
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
                // bis hier
            } catch (UnknownHostException exc){ // Falls bei der Verbindung ein Fehler auftritt
                exc.printStackTrace(); // Fehleranalyse wird ausgegeben
                isCurrentVpsDone = true;
                error = true;
            }

            if(!error) {
                // BufferedReader scannt die HTML Zeile für Zeile
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine = in.readLine();

                if(downloaded == 0) {
                    emptyArrays();
                    hasData = true;
                }

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
                        String day = inputLine.substring(8);
                        System.out.println(day);


                        // Speichert das Datum und den Wochentag von bis zu "amountPlans" Plänen. Text wird auf den Buttons zum navigieren benutzt
                        // Datum wird in originalem Format benötigt, um auf die Seite eines bestimmten Tages zugreifen zu können
                        while (slot < amountPlans && !found) {
                            if (currentVps[slot].equals("---")) {
                                currentVps[slot] = date;
                                days[slot] = day + ".";
                                found = true;
                                isCurrentVpsDone = true;
                            }
                            slot++;
                        }
                        found = false;
                    }
                    inputLine = in.readLine();
                }
                in.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            // getHtml wird gestartet, aber error wird gespeichert
            isCurrentVpsDone = true;
            error = true;
        }
    }

    // Zugriff auf einen Plan und speichern der obenstehenden Daten
    public void getHtml(){
        while (!isCurrentVpsDone) {
            // wartet bis getCurrentVps() fertig ist
        }
        try {
            BufferedReader in;
            SharedPreferences sharedPreferences = getSharedPreferences(TEST, MODE_PRIVATE);
            if (!sharedPreferences.getBoolean(testMode, false)) {
                System.out.println("going online");
                // Url eines bestimmten Tages ist die Url der Startseite + das Datum des Tages
                String URL = URLvp + "/" + currentVps[downloaded];
                URL obj = new URL(URL);
                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("POST");

                String user_name = nutzername;
                String password = passwort;

                // Passwort der https Seite wird eingegeben
                // Ab hier Code von Chilly Facts
                String userCredentials = user_name + ":" + password;
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
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                // Bis hier
            } else {
                System.out.println(getAssetName(downloaded));
                in = new BufferedReader(new InputStreamReader(getAssets().open(getAssetName(downloaded))));
                System.out.println("got buffered reader");
            }

            if(downloaded == 0){
                list = new ArrayList[amountPlans];
                for(int i = 0; i < amountPlans; i++){
                    list[i] = new ArrayList<>();
                }
            }

            String inputLine = in.readLine();
            //Anfang des Planes wird übersprungen
            while (!inputLine.contains("Vertretungsplan für")) {
                inputLine = in.readLine();
            }

            String tag;
            String version;
            String nachricht;

            // Wochentag und Datum des Tages wird gespeichert
            tag = inputLine;
            int length1 = tag.length();
            String weekday = tag.substring(24, length1 - 21);
            String currentDate = tag.substring(length1 - 15, length1 - 9);
            tag = weekday + " " + currentDate;


            // Suchen der Versionsnummer
            while (!inputLine.contains("Version:")) {
                inputLine = in.readLine();
            }
            int length2 = inputLine.length();
            String number = inputLine.substring(21, length2 - 10);
            inputLine = in.readLine();
            version = "Version: " + number + "," + inputLine.substring(3);

            // Abbruchbedingung, falls keine allgemeinen Benachrichtigungen oben in der Box stehen
            boolean skip = false;
            noData = false;
            while (!inputLine.contains("alert") && !skip) {
                if (inputLine.contains("table-responsive") || inputLine.contains("no-data")) {
                    skip = true;
                    if (inputLine.contains("no-data")) {
                        noData = true;
                    }
                }
                inputLine = in.readLine();
            }
            inputLine = in.readLine();
            if (!skip) {
                nachricht = inputLine;
                int length3 = nachricht.length();
                nachricht = nachricht.substring(4, length3 - 6);
                // Anführungszeichen werden sonst nicht korrekt angezeigt
                nachricht = nachricht.replaceAll("&quot;", "\"");
            } else {
                // Manchmal gibt es im Quellcode eine Box, jedoch ohne Inhalt
                nachricht = "Keine Nachrichten";
            }

            System.out.println("after nachricht " + inputLine);

            // Werden gespeichert auf erstem Punkt
            Datapoint point = new Datapoint(tag, version, nachricht, null, null, null, null, null);
            list[downloaded].add(point);

            saveEvents(in);
            in.close();

        } catch (Exception ex){
            ex.printStackTrace();
            // Im Falle eines Error wird automatisch einmal komplett neu geladen
            if (firstError) {
                reload();
                firstError = false;
            } else {
                // Beim zweiten durchlauf wird der Error gespeichert
                error = true;
            }
        }
    }

    // Alle Ereignisse des jeweiligen Planes werden gespeichert
    public void saveEvents(BufferedReader in){
        // System.out.println("saveEvents");
        try {
            String inputLine = in.readLine();

            System.out.println("first " + inputLine);
            while (!inputLine.contains("Es wurde ein Filter auf")) {
                //System.out.println("empty " + inputLine);
                if (inputLine.contains("index")) {
                    //System.out.println("index " + inputLine);
                    String art;
                    String stunde;
                    String fach;
                    String vertreter;
                    String raum;
                    String klasse;
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
                    } else if(vertreter.contains("span")) { // Vertreter Feld ist leer
                        vertreter = "-";
                    }

                    inputLine = in.readLine();
                    int lengthKlasse = (inputLine).length();
                    klasse = (inputLine).substring(38, lengthKlasse - 5);
                    if (klasse.contains("---")) {
                        klasse = "-";
                    }

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

                    //System.out.println(downloaded + " " + art + stunde + fach + vertreter + klasse + raum + notiz);
                    // Daten werden in Arraylist gespeichert
                    Datapoint point = new Datapoint(art, stunde, fach, vertreter, klasse, raum, notiz, null);
                    list[downloaded].add(point);

                }
                inputLine = in.readLine();
            }
            downloaded++;
            System.out.println("download++ " + downloaded);

        } catch (Exception ex){
            downloaded++;
            ex.printStackTrace();
        }
    }

    // Oberen Daten werden eingetragen unf Filtermodus gewählt
    public void setViews(){
        entfallText = "---";
        filledViews = false;
        while(!emptiedAll){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                emptyTables();
                scrollToZero();
                String day = list[currentPage].get(0).getArt();
                int l = day.length();
                p1Tag.setText(day);
                p1Version.setText(list[currentPage].get(0).getStunde());
                p1Nachrichten.setText(list[currentPage].get(0).getFach());
                p1TagBottom.setText(day.substring(0,l - 7));
                p1KeinEntfall.setVisibility(View.GONE);
                p1Nachrichten.setVisibility(View.VISIBLE);
                p1Tag.setVisibility(View.VISIBLE);
                p1Version.setVisibility(View.VISIBLE);
                p1TagBottom.setVisibility(View.VISIBLE);

                linesFound = false;

                tableKlasse.setMinimumWidth(0);
                resizeScrollables();

                System.out.println(mode + " mode + unterstufe " + unterstufe);
                if (mode == 1 || (unterstufe && mode == 2)) {
                    tableKlasse.setMinimumWidth(p1Fach.getWidth());
                    readDataAll();
                } else if (mode == 2) {
                    if (stufe.equals("---")) {
                        entfallText = "Gib zuerst deine Klasse ein";
                    } else {
                        readDataFiltered();
                    }
                } else {
                    if (stufe.equals("---")) {
                        entfallText = "Gib zuerst deine Klasse ein";
                    } else if (!unterstufe && kurse[0].equals("---")) {
                        entfallText = "Gib zuerst deine Kurse ein";
                    } else {
                        readDataFiltered();
                    }
                }
                filledViews = true;
            }
        });

        while (!filledViews) {
            try {
                Thread.sleep(50);
            } catch (Exception ex) {

            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aktualisiereKeinEntfall();
                updateViewsVisibility();
                updateButtons();
                formeNachricht();
                filledCurrent = true;
                workingShort = false;
            }
        });
    }

    // Fall Modus "Alle", oder "Stufe" bei einem Unterstufenschüler
    public void readDataAll(){
        int i = 1;
        Datapoint point;
        findTables();
        while(i < list[currentPage].size()){
            point = list[currentPage].get(i);
            if(mode == 1 || point.getKlasse().contains(stufe.substring(0,2))){
                fillViews(point.getArt(), point.getStunde(), point.getFach(), point.getLehrer(), point.getRaum(), point.getNotiz(), point.getKlasse());
                linesFound = true;
            }
            i++;
        }
    }

    // Daten werden gefiltert angezeigt
    public void readDataFiltered(){
        int i = 1;
        Datapoint point;
        findTables();
        tableRaum = findViewById(R.id.p1TableRaumWithout);
        // tableNotiz = findViewById(R.id.p1TableNotizWithout);
        while(i < list[currentPage].size()){
            point = list[currentPage].get(i);
            if (point.getKlasse().contains(stufe)) {
                // System.out.println(point.getStunde() + "   anz: " + anzahlKurse);

                boolean kursFound = false;
                if (!unterstufe && mode == 3) {
                    // Bei der Oberstufe muss zusätzlich geprüft werden, ob es einer der eingegebenen Kurse ist
                    int integer = 0;
                    String fach = point.getFach();
                    while (integer < anzahlKurse) {
                        if (fach.contains(kurse[integer])) {
                            kursFound = true;
                        }
                        integer++;
                    }
                }

                if (unterstufe || kursFound || mode != 3) {
                    fillViews(point.getArt(), point.getStunde(), point.getFach(), point.getLehrer(), point.getRaum(), point.getNotiz(), null);
                    linesFound = true;
                }
            }
            i++;
        }
    }

    // Filtermodus wird gewechselt
    public void modeSwitch(View view) {
        // Falls kein anderer Prozess am laufen ist
        if (!working) {
            // Wechsle den Modus
            mode--; // 1=alle, 2=stufe, 3=eigene
            if (mode == 0) {
                mode = 3;
            }
            newModeText = false;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            p1Mode.setText(modeText(mode));
                            newModeText = true;
                        }
                    });
                    while(!newModeText){
                        // warte bis Feld aktualisiert wurde
                    }
                    setViews(); // Lade neue Daten
                }
            });
            thread.start();
        }
    }

    // Text des Filterbuttons wird angepasst
    public String modeText(int m) {
        if (m == 1) {
            return "alle";
        } else if (m == 2) {
            return "stufe";
        } else {
            return "eigene";
        }
    }

    // Kein-Entfall-Button wird aktualisiert
    public void aktualisiereKeinEntfall() {
        p1Nachrichten.setVisibility(View.VISIBLE);
        boolean show = true;

        if (entfallText.equals("---")) {
            if (!linesFound) {
                p1KeinEntfall.setTextSize(22);
                entfallText = "Heute entfallen keine Stunden";
            }
            if (noData) {
                entfallText = "Für heute wurde noch\nnichts eingetragen ";
            }
            if (!currentVps[1].equals("---")) {
                entfallText = entfallText + "\nÜberprüfe auch die anderen Tage";
            }
            if (currentVps[0].equals("---")) {
                entfallText = "Es sind zurzeit keine Vertretungspläne in dem System vorhanden";
                show = false;
            }
        }
        Toast t = Toast.makeText(this, "Aktualisiert", Toast.LENGTH_SHORT);
        if (error && downloaded == 0) {
            t.setText("#ERROR");
            entfallText = "#ERROR:\nEs gab einen Verbindungsfehler!";
            show = false;
        }
        if (error && downloaded == 0) {
            t.show();
        }
        if (!entfallText.equals("---")) {
            p1KeinEntfall.setText(entfallText);
        }
        if (show) {
            p1Mode.setVisibility(View.VISIBLE);
        } else {
            p1Mode.setVisibility(View.INVISIBLE);
        }
    }

    // Tabelle werden initialisiert
    public void findTables() {
        tableArt = findViewById(R.id.p1TableArt);
        tableStunde = findViewById(R.id.p1TableStunde);
        tableFach = findViewById(R.id.p1TableFach);
        tableVertreter = findViewById(R.id.p1TableLehrer);
        tableKlasse = findViewById(R.id.p1TableKlasse);
        tableRaum = findViewById(R.id.p1TableRaumWith);
    }

    // Eine neue Reihe der Tabelle wird erzeugt
    public void fillViews(final String art, final String stunde, final String fach, final String vertreter, final String raum, final String notiz, final String klasse) {
        // System.out.println("filled " + art + stunde + fach + vertreter + raum + notiz);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
        final boolean dark = prefs.getBoolean(darkMode, false);
        if(dark){
            theme.resolveAttribute(R.attr.onEdgeBottom, typedValue, true);
        } else {
            theme.resolveAttribute(R.attr.onAccentTop, typedValue, true);
        }
        @ColorInt int color = typedValue.data;

        TableRow row1 = new TableRow(MainActivity.this);
        row1.setBackgroundResource(R.drawable.chart_bottom_first);
        TextView view1 = new TextView(MainActivity.this);
        view1.setTextColor(color);
        view1.setSingleLine(true);
        view1.setText(art);
        row1.addView(view1);
        tableArt.addView(row1);
        final View vArt = row1;
        lastView = row1;

        TableRow row2 = new TableRow(MainActivity.this);
        row2.setBackgroundResource(R.drawable.chart_bottom);
        TextView view2 = new TextView(MainActivity.this);
        view2.setTextColor(color);
        view2.setSingleLine(true);
        view2.setText(stunde);
        row2.addView(view2);
        tableStunde.addView(row2);

        TableRow row3 = new TableRow(MainActivity.this);
        row3.setBackgroundResource(R.drawable.chart_bottom);
        TextView view3 = new TextView(MainActivity.this);
        view3.setTextColor(color);
        view3.setSingleLine(true);
        view3.setText(fach);
        row3.addView(view3);
        tableFach.addView(row3);

        TableRow row4 = new TableRow(MainActivity.this);
        row4.setBackgroundResource(R.drawable.chart_bottom);
        TextView view4 = new TextView(MainActivity.this);
        view4.setTextColor(color);
        view4.setSingleLine(true);
        view4.setText(vertreter);
        row4.addView(view4);
        tableVertreter.addView(row4);

        TableRow row5 = new TableRow(MainActivity.this);
        row5.setBackgroundResource(R.drawable.chart_bottom_last);

        TextView view5 = new TextView(MainActivity.this);
        view5.setTextColor(color);
        view5.setSingleLine(true);
        view5.setText(raum);
        row5.addView(view5);

        TableLayout.LayoutParams paramR = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view1.getHeight());
        View view6;
        if (notiz.equals("---")) {
            view6 = new TextView(MainActivity.this);
            ((TextView) view6).setTextColor(getResources().getColor(R.color.transparent));
            ((TextView) view6).setText("x");
        } else {
            view6 = new ImageView(MainActivity.this);
            ((ImageView) view6).setImageDrawable(getResources().getDrawable(android.R.drawable.ic_dialog_info));
            TableRow.LayoutParams paramV = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            paramV.gravity = Gravity.CENTER;
            view6.setLayoutParams(paramV);
            int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
            view6.setPadding(pixel,0,0,0);
            final String text = notiz;
            View.OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notizPopup(text, vArt);
                }
            };
            row5.setOnClickListener(click);
        }
        row5.setLayoutParams(paramR);
        row5.addView(view6);
        tableRaum.addView(row5);

        TableRow row8 = new TableRow(MainActivity.this);
        if (klasse != null) {
            row8.setBackgroundResource(R.drawable.chart_bottom);
            TextView view8 = new TextView(MainActivity.this);
            view8.setTextColor(color);
            view8.setSingleLine(true);
            view8.setText(klasse);
            row8.addView(view8);
            tableKlasse.addView(row8);
        }

        int pixel2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        TableLayout.LayoutParams params = (TableLayout.LayoutParams) row1.getLayoutParams();
        params.setMargins(0,0,0, pixel2);
        row1.setLayoutParams(params);
        row2.setLayoutParams(params);
        row3.setLayoutParams(params);
        row4.setLayoutParams(params);
        row5.setLayoutParams(params);
        row8.setLayoutParams(params);
    }

    // Restlichen Pläne werden heruntergeladen
    public void downloadAll(){
        while (downloaded < amountPlans && !currentVps[downloaded].equals("---")){
            getHtml();
        }
    }

    // Scrollfelder scrollen zum Startpunkt
    public void scrollToZero() {
        p1ScrollNachricht.scrollTo(0, 0);
        p1ScrollInner.scrollTo(0, 0);
        p1ScrollHorizontal.scrollTo(0, 0);
    }

    // Inhalte der Tabellen werden geleert
    public void emptyTables() {
        findTables();
        TableLayout raumWithout = findViewById(R.id.p1TableRaumWithout);
        tableArt.removeAllViews();
        tableStunde.removeAllViews();
        tableFach.removeAllViews();
        tableVertreter.removeAllViews();
        tableKlasse.removeAllViews();
        tableRaum.removeAllViews();
        raumWithout.removeAllViews();
    }

    // Bausteine der  GUI werden initialsiert
    public void findViews() {
        p1Tag = findViewById(R.id.p1Tag);
        p1Version = findViewById(R.id.p1Version);
        p1Nachrichten = findViewById(R.id.p1Nachrichten);
        p1TagDavorText = findViewById(R.id.p1TagDavorText);
        p1TagDanachText = findViewById(R.id.p1TagDanachText);
        p1TagBottom = findViewById(R.id.p1TagBottom);
        p1KeinEntfall = findViewById(R.id.p1KeinEntfall);
        p1NeuLaden = findViewById(R.id.p1NeuLaden);
        p1Mode = findViewById(R.id.p1Mode);

        p1Art = findViewById(R.id.p1Art);
        p1Stunde = findViewById(R.id.p1Stunde);
        p1Fach = findViewById(R.id.p1Fach);
        p1Vertreter = findViewById(R.id.p1Vertreter);
        p1Klasse = findViewById(R.id.p1Klasse);
        p1Raum = findViewById(R.id.p1RaumWith);
        p1Notiz = findViewById(R.id.p1NotizWith);

        p1ScrollNachricht = findViewById(R.id.p1ScrollNachricht);
        p1ScrollInner = findViewById(R.id.p1ScrollInner);
        p1ScrollHorizontal = findViewById(R.id.p1ScrollHorizontal);
    }

    public void tableTopInvisible() {
        (findViewById(R.id.p1RaumWithout)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.p1NotizWithout)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.p1RaumWith)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.p1NotizWith)).setVisibility(View.INVISIBLE);
        p1Art.setVisibility(View.INVISIBLE);
        p1Stunde.setVisibility(View.INVISIBLE);
        p1Fach.setVisibility(View.INVISIBLE);
        p1Vertreter.setVisibility(View.INVISIBLE);
        p1Klasse.setVisibility(View.INVISIBLE);
    }

    // Sichtbarkeit von Feldern wird angepasst
    public void updateViewsVisibility() {
        // Felder ohne Daten sind unsichtbar und Felder mit Daten sichbar
        tableTopInvisible();
        if (!linesFound) {
            p1KeinEntfall.setVisibility(View.VISIBLE);
            p1ScrollHorizontal.setVisibility(View.INVISIBLE);
        } else {
            // Tabellenkopf ist auch unsichtbar, wenn es keine Ereignisse gibt
            p1KeinEntfall.setVisibility(View.INVISIBLE);
            p1Art.setVisibility(View.VISIBLE);
            p1Stunde.setVisibility(View.VISIBLE);
            p1Fach.setVisibility(View.VISIBLE);
            p1Vertreter.setVisibility(View.VISIBLE);
            p1ScrollHorizontal.setVisibility(View.VISIBLE);
            if (mode == 1 || (unterstufe && mode == 2)) {
                System.out.println("with klasse");
                p1Klasse.setVisibility(View.VISIBLE);
                p1Raum = findViewById(R.id.p1RaumWith);
                p1Notiz = findViewById(R.id.p1NotizWith);
            } else {
                System.out.println("without klasse");
                p1Klasse.setText("");
                p1Klasse.setVisibility(View.INVISIBLE);
                p1Raum = findViewById(R.id.p1RaumWithout);
                p1Notiz = findViewById(R.id.p1NotizWithout);
            }
            p1Raum.setVisibility(View.VISIBLE);
            p1Notiz.setVisibility(View.VISIBLE);
            p1Klasse.setText("Klasse");
        }
    }

    // Die zwei Buttons unten werden aktualisiert
    public void updateButtons() {
        if (currentPage > 0) {
            String day = days[currentPage - 1];
            int length = day.length();
            // Falls die Version >= 10, dann verschiebt sich der substring um einen Charakter, was das Datum und den Tag falsch ausschneidet
            // Falls also das "v" von Version in dem substring enthalten ist, verschiebt sich der substring um 1, bzw. "sub" Einheiten
            if (!day.substring(length - 7, length - 5).contains("v")) {
                length--;
            }
            String dayBefore = day.substring(0, length - 15);
            p1TagDavorText.setText(dayBefore);
            p1TagDavorText.setVisibility(View.VISIBLE);
        } else {
            p1TagDavorText.setVisibility(View.INVISIBLE);
        }

        int nextDay = currentPage + 1;
        if (nextDay < amountPlans && !days[currentPage + 1].equals("---")) {
            String day = days[currentPage + 1];
            int length = day.length();
            int sub = 1;
            if (day.substring(length - 7, length - 5).contains("v")) {
                sub = 0;
            }
            String dayAfter = day.substring(0, length - 15 - sub);
            p1TagDanachText.setText(dayAfter);
            p1TagDanachText.setVisibility(View.VISIBLE);
        } else {
            p1TagDanachText.setVisibility(View.INVISIBLE);
        }
    }

    // Popup, welches die Nachricht eines Lehrers anzeigt, falls vorhanden
    public void notizPopup(final String message, View YPoint) {
        // Das layout "Popup-Notiz" wird verwendet
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_notiz, null);

        p1Nachrichten = findViewById(R.id.p1Nachrichten);
        int width = p1Nachrichten.getMeasuredWidth() - 20;
        popupView.measure(View.MeasureSpec.makeMeasureSpec(mainLayout.getWidth(), View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ((TextView) popupWindow.getContentView().findViewById(R.id.p1PopupText)).setText(message);

        // Popup muss nach oben verschoben werden, damit es nich unter, sondern etwas über dem Icon angezeigt wird
        int yOffset = Math.round(YPoint.getHeight() + popupView.getMeasuredHeight());
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        int xOffset = (int) p1Notiz.getX() - width + p1Notiz.getMeasuredWidth() - pixel;
        popupWindow.showAsDropDown(YPoint, xOffset, -yOffset);

        // Popup Window verschwindet, wenn der Bildschirm berührt wird
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    // Popup Fenster, das angezeigt wird bei dem ersten Start der App
    // Nutzer muss Nutzernamen und Passwort des Vertretungsplanes eingeben
    public void introPopup() {
        TextView introHeader = findViewById(R.id.introHeader);
        TextView introText = findViewById(R.id.introText);
        TextView introUsername = findViewById(R.id.introUsername);
        TextView introPassword = findViewById(R.id.introPassword);
        EditText introUsernameEdit = findViewById(R.id.introUsernameEdit);
        EditText introPasswordEdit = findViewById(R.id.introPasswordEdit);
        Button introButton = findViewById(R.id.introButton);
        Button introButtonTest = findViewById(R.id.introButtonTest);

        introHeader.setVisibility(View.VISIBLE);
        introText.setVisibility(View.VISIBLE);
        introUsername.setVisibility(View.VISIBLE);
        introPassword.setVisibility(View.VISIBLE);
        introUsernameEdit.setVisibility(View.VISIBLE);
        introPasswordEdit.setVisibility(View.VISIBLE);
        introButton.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(TEST, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(testMode, false)) {
            introButtonTest.setVisibility(View.VISIBLE);
        } else {
            introButtonTest.setVisibility(View.INVISIBLE);
        }
    }

    // Tastatur wird versteckt
    // Code dieser Methode aus dem Internet kopiert
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // Nutzer muss Tastatur manuell verstecken
        }
    }

    // "OK" Knopf bei erster Anmeldung, der überprüft, ob Nutzername und Passwort richtig sind, oder es einen Verbindungsfehler gibt
    // Sind Nutzername und Passwort richtig, so werden diese dauerhaft gespeichert
    public void introButton(View view) {
        // Eingaben werden auf lokale Variablen gespeichert
        EditText introUsernameEdit = findViewById(R.id.introUsernameEdit);
        EditText introPasswordEdit = findViewById(R.id.introPasswordEdit);
        TextView introText = findViewById(R.id.introText);
        final String username = introUsernameEdit.getText().toString().toLowerCase();
        final String password = introPasswordEdit.getText().toString().toLowerCase();
        introText.setText("");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Es wird versucht auf die Startseite zuzugreifen
                try {
                    String URL = URLvp;
                    URL obj = new URL(URL);
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    String userCredentials = username + ":" + password;
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

                    // Es wird gepüft, ob anhand der Anmeldedaten die Webseite erreicht wird
                    if (in.readLine().contains("DOCTYPE html")) {
                        // Ist der Zugriff erfolgreich, ist der Anmeldeprozess fertig
                        hideKeyboard();
                        SharedPreferences sharedPreferences0 = getSharedPreferences(PREFS0, MODE_PRIVATE);
                        SharedPreferences sharedPreferences1 = getSharedPreferences(USERNAME, MODE_PRIVATE);
                        SharedPreferences sharedPreferences2 = getSharedPreferences(PASSWORD, MODE_PRIVATE);

                        SharedPreferences.Editor editor0 = sharedPreferences0.edit();
                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                        SharedPreferences.Editor editor2 = sharedPreferences2.edit();

                        // Nutzername und Passwort werden letzendlich gespeichert
                        editor0.putString(firstLaunch, "false").apply();
                        editor1.putString(user, username).apply();
                        editor2.putString(pass, password).apply();

                        // Die "Kurse Ändern" wird wieder im Main Thread geöffnet. Dort wird um die Eingabe der Klasse/Stufe und ggf. der Kurse gebeten
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, KurseAendernPage.class);
                                startActivity(intent);
                                // Anmeldeseite wird vom Activity stack gelöscht
                                finish();
                            }
                        });
                    }
                    in.close();

                } catch (final Exception ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView introText = findViewById(R.id.introText);
                            if (ex instanceof FileNotFoundException) {
                                // Falls die Anmeldedaten nicht korrekt sind
                                introText.setText("Nutzername oder Passwort ungültig");
                                introText.setTextColor(getResources().getColor(R.color.errorRed));
                            } else if (ex instanceof UnknownHostException) {
                                // Falls es einen Verbindungsfehler gibt
                                introText.setText("Verbindung fehlgeschlagen");
                                introText.setTextColor(getResources().getColor(R.color.errorRed));
                            } else {
                                // bei sonstigen Fehlern
                                ex.printStackTrace();
                                introText.setText("#ERROR");
                                introText.setTextColor(getResources().getColor(R.color.errorRed));
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    // Testmodus: Intro-Seite wird aufgerufen
    public void testIntro(View view) {
        SharedPreferences sharedPreferences0 = getSharedPreferences(PREFS0, MODE_PRIVATE);
        SharedPreferences.Editor editor0 = sharedPreferences0.edit();
        editor0.putString(firstLaunch, "false").apply();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, KurseAendernPage.class);
                startActivity(intent);
                // Anmeldeseite wird vom Activity stack gelöscht
                finish();
            }
        });
    }

    // Testmodus: Gespeicherte Daten werden angezeigt
    public void setTestVps() {
        days[0] = "Montag 16.12, v14</a>.";
        days[1] = "Dienstag 17.12, v7</a>.";
        days[2] = "Mittwoch 18.12, v2</a>.";
        days[3] = "Donnerstag 19.12, v44</a>.";
        days[4] = "Freitag 20.12, v11</a>.";

        currentVps[0] = "12345";
        currentVps[1] = "12345";
        currentVps[2] = "12345";
        currentVps[3] = "12345";
        currentVps[4] = "12345";
    }

    // Testmodus: Gespeicherte Daten werden angezeigt
    public String getAssetName(int page) {
        if (page == 0) {
            return "montag";
        } else if (page == 1) {
            return "dienstag";
        } else if (page == 2) {
            return "mittwoch";
        } else if (page == 3) {
            return "donnerstag";
        } else {
            return "freitag";
        }
    }

    // Menü popup wird angezeigt
    public void menu(View view){
        switchesInvisible = true;
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_menu, null);

        final PopupWindow popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        popupWindow2.showAtLocation(popupView, Gravity.LEFT, 0, 0);

        TextView text = popupWindow2.getContentView().findViewById(R.id.textProfil);
        text.setText("Profil & Kurse");

        final ImageView imageMenu = popupWindow2.getContentView().findViewById(R.id.imageMenu);
        final TableRow profil = popupWindow2.getContentView().findViewById(R.id.row1);
        final TableRow message = popupWindow2.getContentView().findViewById(R.id.row2);
        final TableRow darkMode = popupWindow2.getContentView().findViewById(R.id.row3);
        final TableRow about = popupWindow2.getContentView().findViewById(R.id.row4);
        final ImageView darkModeOn = popupWindow2.getContentView().findViewById(R.id.darkModeOn);
        final ImageView darkModeOff = popupWindow2.getContentView().findViewById(R.id.darkModeOff);

        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.dismiss();
            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kurseAendern(profil);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.dismiss();
                notifsButton(message);
            }
        });

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDarkModeVisible(popupWindow2);
            }
        });

        darkModeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDarkMode(popupWindow2, v);
            }
        });

        darkModeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDarkMode(popupWindow2, v);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow2.dismiss();
                infosButton(about);
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // Button öffnet ein Popup mit Infos über die App, wie z.B. Entwickler und Version
    public void infosButton(View view) {
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_kurse, null);

        popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        // popupWindow2.setBackgroundDrawable(getDrawable(R.drawable.background_dim));

        // Versions-Nummer wird aus build.gradle Datei geladen
        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String versionName = "unbekannt";

        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // es wird "unbekannt" angezeigt
        }

        String text = "Schule Gymnasium Odenthal";
        text = text + "\nVersion " + versionName;
        text = text + "\nDesign by Caspar Münzinger";
        text = text + "\nMade by Noel Billing";

        ((TextView) popupWindow2.getContentView().findViewById(R.id.p2PopupText)).setText(text);
        ((Button) popupWindow2.getContentView().findViewById(R.id.p2PopupBehalten)).setText("Schließen");
        ((Button) popupWindow2.getContentView().findViewById(R.id.p2PopupLoeschen)).setText("VpSystems");

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        // prevent clickable background
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow2.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button p2PopupBehalten = popupView.findViewById(R.id.p2PopupBehalten);
        Button p2PopupLoeschen = popupView.findViewById(R.id.p2PopupLoeschen);

        // Popup schließt sich einfach
        p2PopupBehalten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
            }
        });

        // Button, der zu der VP Website führt
        p2PopupLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
                toLink();
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // Popup zur Einstellung von Benachrichtigungen
    public void notifsButton(View view) {
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_notifications, null);

        popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        // prevent clickable background
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow2.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        TextView dailyButton = popupWindow2.getContentView().findViewById(R.id.dailyButton);
        SharedPreferences prefs = getSharedPreferences(TIME, MODE_PRIVATE);
        if(prefs.getString(dailyTime, "off").equals("off")) {
            dailyButton.setBackground(getDrawable(R.drawable.button_off));
        } else {
            dailyButton.setBackground(getDrawable(R.drawable.button_primary));
            dailyButton.setText("Tägliche\nNachricht um " + prefs.getString(dailyTime, "off"));
        }
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTime();
            }
        });

        RadioButton radioImmer = popupWindow2.getContentView().findViewById(R.id.radioImmer);
        RadioButton radioWlan = popupWindow2.getContentView().findViewById(R.id.radioWlan);
        RadioButton radioNie = popupWindow2.getContentView().findViewById(R.id.radioNie);
        SharedPreferences pref = getSharedPreferences(NETWORK, MODE_PRIVATE);
        int num = pref.getInt(netMode, 2);
        switch (num) {
            case 0:
                radioImmer.setChecked(true);
                break;
            case 1:
                radioWlan.setChecked(true);
                break;
            case 2:
                radioNie.setChecked(true);
                break;
        }

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // Falls eine Einstellung bei Benachrichtigungen gewählt wurde
    public void onRadioButtonClicked(View view) {
        RadioButton radioImmer = popupWindow2.getContentView().findViewById(R.id.radioImmer);
        RadioButton radioWlan = popupWindow2.getContentView().findViewById(R.id.radioWlan);
        RadioButton radioNie = popupWindow2.getContentView().findViewById(R.id.radioNie);

        SharedPreferences sharedPrefs = getSharedPreferences(NETWORK, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        // Überprüft welcher Knopf gedrückt wurde
        switch(view.getId()) {
            case R.id.radioImmer:
                radioImmer.setChecked(true);
                radioWlan.setChecked(false);
                radioNie.setChecked(false);
                editor.putInt(netMode, 0).apply();
                scheduleJob(view);
                break;
            case R.id.radioWlan:
                radioImmer.setChecked(false);
                radioWlan.setChecked(true);
                radioNie.setChecked(false);
                editor.putInt(netMode, 1).apply();
                scheduleJob(view);
                break;
            case R.id.radioNie:
                radioImmer.setChecked(false);
                radioWlan.setChecked(false);
                radioNie.setChecked(true);
                editor.putInt(netMode, 2).apply();
                cancelJob(view);
                break;
        }

        Toast.makeText(this, "Einstellung aktualisiert", Toast.LENGTH_SHORT).show();
    }

    // Hintergrunaktualisierung wird eingeleitet
    // Code teilweise aus dem Internet
    public void scheduleJob(View view){
        cancelJob(view);
        ComponentName comp = new ComponentName(this, BackgroundJob.class);
        // any = 0, unmetered = 1, no Job = 2
        JobInfo info;
        // Mit jeglicher Verbindung
        if(view.getId() == R.id.radioImmer){
            info = new JobInfo.Builder(repeatedJobId, comp)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(1000*60*30)
                    .setPersisted(true)
                    .build();
        } else {
            // Nur mit Wlan
            info = new JobInfo.Builder(repeatedJobId, comp)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(1000*60*30)
                    .setPersisted(true)
                    .build();
        }

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int success = scheduler.schedule(info);
        if(success == JobScheduler.RESULT_SUCCESS){
            Log.d(Tag, "Job scheduled");
        } else {
            Log.d(Tag, "Job scheduling failed");
        }
    }

    // Hintergrundaktualisierung wird dauerhaft unterbrochen
    public void cancelJob(View view){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if(scheduler != null) {
            scheduler.cancel(repeatedJobId);
        }
        Log.d(Tag, "Job cancelled");
    }

    // Pläne werden jeden Tag zu bestimmter Zeit abgerufen
    public void setDailyTimer(int h, int m){
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,h);
        calendar.set(Calendar.MINUTE,m);

        Intent myIntent = new Intent(this, DailyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,dailyJobId,myIntent,0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        System.out.println("scheduled Alarm");
    }

    // Tägliche Aktualisierung wird dauerhaft unterbrochen
    public void cancelDaily(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), DailyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), dailyJobId, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    // Falls der große Button der Benachrichtigungsseite gedrückt wurde
    public void handleTime(){
        SharedPreferences prefs = getSharedPreferences(TIME, MODE_PRIVATE);
        if(prefs.getString(dailyTime, "off").equals("off")){
            popupTime();
        } else {
            popupCancelTime();
        }
    }

    // Auswahlfeld für Zeit wird angezeigt
    // Code teilweise aus dem Internet
    public void popupTime(){
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String h = ((Integer) selectedHour).toString();
                String m = ((Integer) selectedMinute).toString();
                if(h.length() == 1){
                    h = "0" + h;
                }
                if(m.length() == 1){
                    m = "0" + m;
                }

                System.out.println(h + ":" + m);
                SharedPreferences prefs = getSharedPreferences(TIME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                String t = h + ":" + m;
                editor.putString(dailyTime, t).apply();
                setDailyTimer(selectedHour, selectedMinute);
                popupWindow2.dismiss();
                notifsButton(null);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Wähle eine Uhrzeit");
        mTimePicker.show();
    }

    // Fragt ob tägliche Benachrichtigungseinstellung unterbrochen werden soll
    public void popupCancelTime(){
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_kurse, null);
        findViews();

        // Neues Popup Window wird Initialisiert
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        String message = "Tägliche Benachrichtigung deaktivieren?";
        ((TextView) popupWindow.getContentView().findViewById(R.id.p2PopupText)).setText(message);

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button deaktivieren = popupView.findViewById(R.id.p2PopupLoeschen);
        Button behalten = popupView.findViewById(R.id.p2PopupBehalten);
        behalten.setText("behalten");
        deaktivieren.setText("OK");

        behalten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        deaktivieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(TIME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(dailyTime, "off").apply();
                cancelDaily();
                popupWindow2.dismiss();
                popupWindow.dismiss();
                notifsButton(null);
            }
        });
    }

    // Sichtbarkeit des Darkmode Knopfes wir angepasst
    public void setDarkModeVisible(PopupWindow popup){
        ImageView darkModeOn = popup.getContentView().findViewById(R.id.darkModeOn);
        ImageView darkModeOff = popup.getContentView().findViewById(R.id.darkModeOff);

        darkModeOn.setVisibility(View.GONE);
        darkModeOff.setVisibility(View.GONE);

        if(switchesInvisible) {
            SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
            boolean dark = prefs.getBoolean(darkMode, false);
            if (dark) {
                darkModeOn.setVisibility(View.VISIBLE);
            } else {
                darkModeOff.setVisibility(View.VISIBLE);
            }
            switchesInvisible = false;
        } else {
            switchesInvisible = true;
        }
    }

    // Farbmodus wird gewechselt
    public void setDarkMode(PopupWindow popup, View v){
        boolean dark = false;
        if(v.getId() == R.id.darkModeOff){
            dark = true;
        }
        SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(darkMode, dark).apply();

        ImageView darkModeOn = popup.getContentView().findViewById(R.id.darkModeOn);
        ImageView darkModeOff = popup.getContentView().findViewById(R.id.darkModeOff);
        if(dark){
            darkModeOn.setVisibility(View.VISIBLE);
            darkModeOff.setVisibility(View.INVISIBLE);
            // switch to dark
            setTheme(R.style.doodleDark);
            recreate();
        } else {
            darkModeOn.setVisibility(View.INVISIBLE);
            darkModeOff.setVisibility(View.VISIBLE);
            // switch to light
            setTheme(R.style.doodleLight);
            recreate();
        }
    }

    // Startseite des Vertretungsplanes wird geöffnet
    public void toLink() {
        String url = "https://vp.gymnasium-odenthal.de/god";
        try {
            Uri uriUrl = Uri.parse(url);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } catch (Exception ex) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

}