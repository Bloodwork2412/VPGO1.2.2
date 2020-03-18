
/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Klasse der Profilseite der App

package com.example.vp_go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.Character;
import java.util.Calendar;

import static com.example.vp_go.MainActivity.PREFS0;
import static com.example.vp_go.MainActivity.firstLaunch;


public class KurseAendernPage extends AppCompatActivity {

    // Objekte der UI
    private EditText p2Klasse;
    private TextView p2Kurs1, p2Kurs2, p2Kurs3, p2Kurs4, p2Kurs5, p2Kurs6, p2Kurs7, p2Kurs8, p2Kurs9, p2Kurs10, p2Kurs11, p2Kurs12, p2Kurs13, p2Kurs14, p2AlleKurse, p2KurseAendern;
    private ScrollView p2ScrollView;

    // Shared preferences - speichern Kurse und die Klasse
    public static String DARKMODE = "darkmode";
    public static String NETWORK = "network";
    public static String TIME = "time";
    public static String TEST = "test";
    public static String PREFS = "shared preferences";
    public static String PREFS1 = "shared preferences1";
    public static String PREFS2 = "shared preferences2";
    public static String PREFS3 = "shared preferences3";
    public static String PREFS4 = "shared preferences4";
    public static String PREFS5 = "shared preferences5";
    public static String PREFS6 = "shared preferences6";
    public static String PREFS7 = "shared preferences7";
    public static String PREFS8 = "shared preferences8";
    public static String PREFS9 = "shared preferences9";
    public static String PREFS10 = "shared preferences10";
    public static String PREFS11 = "shared preferences11";
    public static String PREFS12 = "shared preferences12";
    public static String PREFS13 = "shared preferences13";
    public static String PREFS14 = "shared preferences14";

    public static final String darkMode = "dark";
    public static final String netMode = "net";
    public static final String dailyTime = "off";
    public static final String testMode = "mode";
    public static final String klasse = "";
    //Default-kursnamen
    public static final String kurs1 = "+";
    public static final String kurs2 = "+";
    public static final String kurs3 = "+";
    public static final String kurs4 = "+";
    public static final String kurs5 = "+";
    public static final String kurs6 = "+";
    public static final String kurs7 = "+";
    public static final String kurs8 = "+";
    public static final String kurs9 = "+";
    public static final String kurs10 = "+";
    public static final String kurs11 = "+";
    public static final String kurs12 = "+";
    public static final String kurs13 = "+";
    public static final String kurs14 = "+";


    public static int repeatedJobId = 30;
    public static int dailyJobId = 31;
    private static final String Tag = "KurseAendernPage";

    private int kurseGeloescht;
    private int kursClicked; // Nummer des angeklickten Kurses
    private boolean wrongFormat; // Kursformat unbekannt
    private boolean callPopup; // Steht der Kurs im falschen Format, wir ein Popup aufgerufen
    private boolean keepEingabe; // Popup zur Kurseingabe wird nicht geschlossen true
    private boolean isBlocked;
    private boolean firstKlasse;
    private boolean switchesInvisible;
    private PopupWindow popupWindow2;
    private PopupWindow popupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
        boolean dark = prefs.getBoolean(darkMode, false);
        if(dark){
            setTheme(R.style.doodleDark);
        } else {
            setTheme(R.style.doodleLight);
        }
        setContentView(R.layout.activity_kurse_aendern_page);

        replaceKurse();

        firstKlasse = true;
        findViews(); // Initialisierung der Views
        updateViewsText(); // Text von shared preferences einsetzen
        updateViewsVisibility(); // falls ein Kursfeld leer ist, wird es nicht angezeigt
        checkIfKurseVorhanden(); // Überprüft, ob bereits Kurse eingegeben wurden, falls der Nutzer in der Oberstufe ist
        updateScroller();
        keepEingabe = false;
        kurseGeloescht = 0;

        SharedPreferences tst = getSharedPreferences(TEST, MODE_PRIVATE);
        View p2Test = findViewById(R.id.p2Test);
        if (tst.getBoolean(testMode, false)) {
            p2Test.setVisibility(View.VISIBLE);
        }

        // Bittet Nutzer, eine Klasse/Stufe anzugeben, falls noch keine gespeichert wurde
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sharedPreferences.getString(klasse, "").equals("")) {
            p2KurseAendern = findViewById(R.id.p2KurseAendern);
            p2KurseAendern.setText("Gib zuerst deine\nKlasse/Stufe ein.");

            ConstraintLayout activity_kurseAendern = findViewById(R.id.activity_kurse_aendern_page);
            activity_kurseAendern.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(firstKlasse) {
                        firstKlasse = false;
                        popupKlasse(null);
                    }
                }
            });
        }

        // Kursfeld kann gescrollt werden falls zu viele Kurse vorhanden sind
        p2ScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isBlocked;
            }
        });

    }

    // Methode nur nötig bei neuem Update
    // Ersetzt Zeichen für einen "leeren" Kurs
    public void replaceKurse(){
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

        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        SharedPreferences.Editor editor4 = sharedPreferences4.edit();
        SharedPreferences.Editor editor5 = sharedPreferences5.edit();
        SharedPreferences.Editor editor6 = sharedPreferences6.edit();
        SharedPreferences.Editor editor7 = sharedPreferences7.edit();
        SharedPreferences.Editor editor8 = sharedPreferences8.edit();
        SharedPreferences.Editor editor9 = sharedPreferences9.edit();
        SharedPreferences.Editor editor10 = sharedPreferences10.edit();
        SharedPreferences.Editor editor11 = sharedPreferences11.edit();
        SharedPreferences.Editor editor12 = sharedPreferences12.edit();
        SharedPreferences.Editor editor13 = sharedPreferences13.edit();
        SharedPreferences.Editor editor14 = sharedPreferences14.edit();

        if(sharedPreferences1.getString(kurs1, "+").equals("---")){
            editor1.putString(kurs1, "+").apply();
        }
        if(sharedPreferences2.getString(kurs2, "+").equals("---")){
            editor2.putString(kurs2, "+").apply();
        }
        if(sharedPreferences3.getString(kurs3, "+").equals("---")){
            editor3.putString(kurs3, "+").apply();
        }
        if(sharedPreferences4.getString(kurs4, "+").equals("---")){
            editor4.putString(kurs4, "+").apply();
        }
        if(sharedPreferences5.getString(kurs5, "+").equals("---")){
            editor5.putString(kurs5, "+").apply();
        }
        if(sharedPreferences6.getString(kurs6, "+").equals("---")){
            editor6.putString(kurs6, "+").apply();
        }
        if(sharedPreferences7.getString(kurs7, "+").equals("---")){
            editor7.putString(kurs7, "+").apply();
        }
        if(sharedPreferences8.getString(kurs8, "+").equals("---")){
            editor8.putString(kurs8, "+").apply();
        }
        if(sharedPreferences9.getString(kurs9, "+").equals("---")){
            editor9.putString(kurs9, "+").apply();
        }
        if(sharedPreferences10.getString(kurs10, "+").equals("---")){
            editor10.putString(kurs10, "+").apply();
        }
        if(sharedPreferences11.getString(kurs11, "+").equals("---")){
            editor11.putString(kurs11, "+").apply();
        }
        if(sharedPreferences12.getString(kurs12, "+").equals("---")){
            editor12.putString(kurs12, "+").apply();
        }
        if(sharedPreferences13.getString(kurs13, "+").equals("---")){
            editor13.putString(kurs13, "+").apply();
        }
        if(sharedPreferences14.getString(kurs14, "+").equals("---")){
            editor14.putString(kurs14, "+").apply();
        }

        System.out.println(sharedPreferences1.getString(kurs1, "+"));
        System.out.println(sharedPreferences8.getString(kurs8, "+"));
        System.out.println(sharedPreferences14.getString(kurs14, "+"));
    }

    // Versteckt die Tastatur
    public void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            // Nutzer muss Tastaur manuell verstecken
        }
    }

    // Tastatur wird angezeigt
    public void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            // Nutzer muss Tastaur manuell aufrufen
        }
    }

    // Darf Scrollfeld der Kurse gescrollt werden?
    public void updateScroller() {
        View last;
        int anz = updateAnzahlKurse();
        if (anz < 4) {
            last = p2Kurs1;
        } else if (anz < 7) {
            last = p2Kurs4;
        } else if (anz < 10) {
            last = p2Kurs7;
        } else if (anz < 13) {
            last = p2Kurs10;
        } else {
            last = p2Kurs13;
        }

        isBlocked = !(p2ScrollView.getMeasuredHeight() < (last.getY() + last.getMeasuredHeight()));
    }

    // Anzahl der Kurse wird aktualisiert
    public int updateAnzahlKurse(){
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

        String[] kurse = new String[14];
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

        int anzahlKurse = 13;
        for (int i = 13; i >= 0; i--) {
            if (kurse[i].equals("+")) {
                anzahlKurse = i;
            }
        }
        System.out.println("anzahl " + anzahlKurse);
        return anzahlKurse;
    }

    // Eingegebene Klasse wird überprüft, bearbeitet und ggf. gespeichert
    public void saveKlasse(String input) {
        String noSpace;
        if (input != null) {
            noSpace = input.replaceAll(" ", "");
        } else {
            noSpace = p2Klasse.getText().toString().replaceAll(" ", "");
        }
        noSpace = noSpace.toUpperCase();
        String newKlasse = noSpace;

        if (!noSpace.equals("")) {
            if (noSpace.contains("10")) {
                newKlasse = "EF";
            } else if (noSpace.contains("11")) {
                newKlasse = "Q1";
            } else if (noSpace.contains("12")) {
                newKlasse = "Q2";
            } else {
                noSpace = noSpace.replaceAll("0", "");
                if (Character.isDigit(noSpace.charAt(0))) {
                    int klasse = Character.getNumericValue(noSpace.charAt(0));
                    String abc = noSpace.substring(1);
                    if ((klasse > 4) && (klasse < 10) && isABCD(abc)) {
                        newKlasse = "0" + noSpace;
                    } else {
                        newKlasse = "0";
                    }
                }
            }
            if (!newKlasse.equals("0")) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(klasse, newKlasse).apply();
                Toast.makeText(this, newKlasse + " gespeichert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Klasse nicht erkannt", Toast.LENGTH_SHORT).show();
                p2Klasse.setText("---");
            }
        }
    }

    // Klassenüberprüfung für Unter-/Mittelstufen
    public boolean isABCD(String x) {
        return ((x.equals("A")) || (x.equals("B")) || (x.equals("C")) || (x.equals("D")));
    }

    // setzt den eingegebenen Kurs möglichst in das korrekte Format
    public String modifyKurs(String eingabe) {
        String noSpace = eingabe.replaceAll(" ", "");
        noSpace = noSpace.toUpperCase();
        String newString = eingabe;
        wrongFormat = false;

        if (noSpace.length() == 5) {
            String zk = noSpace.substring(2, 4);
            boolean five5 = Character.isDigit(noSpace.charAt(4));

            // Zusatzkurse z.B. GEZK1
            if (zk.equals("ZK") && five5) {
                newString = noSpace;
            } else {
                wrongFormat = true;
            }
        } else {
            if (noSpace.length() == 4) {
                String four3 = Character.toString(noSpace.charAt(2));
                boolean four4 = Character.isDigit(noSpace.charAt(3));
                // Falls regulärer GK oder LK bestehen aus 4 Zeihen z.b. SW_L1 / PL_G1
                if (four4 && (four3.equals("G") || four3.equals("L"))) {
                    newString = noSpace.substring(0, 2) + " " + noSpace.substring(2);
                    if (newString.substring(0, 2).equals("SO")) {
                        newString = "S0" + newString.substring(2);
                    }
                } else {
                    // Bilinguale Kurse z.B. BIB_1
                    if (four4 && four3.equals("B")) {
                        newString = noSpace.substring(0, 3) + " " + noSpace.substring(3);
                    } else {
                        // Projektkurs z.B. PXC_1
                        if ((noSpace.substring(0, 2).equals("PX") || noSpace.substring(0, 2).equals("VX")) && four4) {
                            newString = noSpace.substring(0, 3) + " " + noSpace.substring(3);
                        } else {
                            wrongFormat = true;
                        }
                    }
                }
            } else {
                if (noSpace.length() == 3) {
                    String three2 = Character.toString(noSpace.charAt(1));
                    boolean three3 = Character.isDigit(noSpace.charAt(2));
                    // Falls regulärer GK oder LK bestehend aus 3 Zeichen z.B. D__G1 / M__L1
                    if (three3 && (three2.equals("G") || three2.equals("L"))) {
                        newString = noSpace.substring(0, 1) + "  " + noSpace.substring(1);
                    } else {
                        wrongFormat = true;
                    }
                } else {
                    // Ist das Kursformat falsch, so wird später ein Popup angezeigt, welches darauf hinweist
                    wrongFormat = true;
                }
            }
        }
        if (wrongFormat) {
            newString = eingabe;
        }
        return newString;
    }

    // Initialisiere alle Views
    public void findViews() {
        p2Klasse = findViewById(R.id.p2Klasse);
        p2AlleKurse = findViewById(R.id.p2AlleKurse);
        p2ScrollView = findViewById(R.id.p2ScrollView);
        p2AlleKurse = findViewById(R.id.p2AlleKurse);

        p2Kurs1 = findViewById(R.id.p2Kurs1);
        p2Kurs2 = findViewById(R.id.p2Kurs2);
        p2Kurs3 = findViewById(R.id.p2Kurs3);
        p2Kurs4 = findViewById(R.id.p2Kurs4);
        p2Kurs5 = findViewById(R.id.p2Kurs5);
        p2Kurs6 = findViewById(R.id.p2Kurs6);
        p2Kurs7 = findViewById(R.id.p2Kurs7);
        p2Kurs8 = findViewById(R.id.p2Kurs8);
        p2Kurs9 = findViewById(R.id.p2Kurs9);
        p2Kurs10 = findViewById(R.id.p2Kurs10);
        p2Kurs11 = findViewById(R.id.p2Kurs11);
        p2Kurs12 = findViewById(R.id.p2Kurs12);
        p2Kurs13 = findViewById(R.id.p2Kurs13);
        p2Kurs14 = findViewById(R.id.p2Kurs14);
    }

    // Text von Shared Preference wird in Textfelder geladen
    public void updateViewsText() {
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

        p2Klasse.setText(sharedPreferences.getString(klasse, "---"));
        p2Kurs1.setText(sharedPreferences1.getString(kurs1, "+"));
        p2Kurs2.setText(sharedPreferences2.getString(kurs2, "+"));
        p2Kurs3.setText(sharedPreferences3.getString(kurs3, "+"));
        p2Kurs4.setText(sharedPreferences4.getString(kurs4, "+"));
        p2Kurs5.setText(sharedPreferences5.getString(kurs5, "+"));
        p2Kurs6.setText(sharedPreferences6.getString(kurs6, "+"));
        p2Kurs7.setText(sharedPreferences7.getString(kurs7, "+"));
        p2Kurs8.setText(sharedPreferences8.getString(kurs8, "+"));
        p2Kurs9.setText(sharedPreferences9.getString(kurs9, "+"));
        p2Kurs10.setText(sharedPreferences10.getString(kurs10, "+"));
        p2Kurs11.setText(sharedPreferences11.getString(kurs11, "+"));
        p2Kurs12.setText(sharedPreferences12.getString(kurs12, "+"));
        p2Kurs13.setText(sharedPreferences13.getString(kurs13, "+"));
        p2Kurs14.setText(sharedPreferences14.getString(kurs14, "+"));
    }

    // Sichtbarkeit von Views und Buttons wird angepasst
    public void updateViewsVisibility() {
        // Leere Kursfelder werden nicht angezeigt
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String klasseNoSpace = sharedPreferences.getString(klasse, "");
        boolean oberstufe;
        if (klasseNoSpace.equals("EF") || klasseNoSpace.equals("Q1") || klasseNoSpace.equals("Q2")) {
            oberstufe = true;
            p2AlleKurse.setVisibility(View.VISIBLE);
        } else {
            oberstufe = false;
            p2AlleKurse.setVisibility(View.INVISIBLE);
        }

        int n = updateAnzahlKurse();

        if (!oberstufe) {
            p2Kurs1.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs1.setVisibility(View.VISIBLE);
        }
        if (n < 1 || !oberstufe) {
            p2Kurs2.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs2.setVisibility(View.VISIBLE);
        }
        if (n < 2 || !oberstufe) {
            p2Kurs3.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs3.setVisibility(View.VISIBLE);
        }
        if (n < 3 || !oberstufe) {
            p2Kurs4.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs4.setVisibility(View.VISIBLE);
        }
        if (n < 4 || !oberstufe) {
            p2Kurs5.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs5.setVisibility(View.VISIBLE);
        }
        if (n < 5 || !oberstufe) {
            p2Kurs6.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs6.setVisibility(View.VISIBLE);
        }
        if (n < 6 || !oberstufe) {
            p2Kurs7.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs7.setVisibility(View.VISIBLE);
        }
        if (n < 7 || !oberstufe) {
            p2Kurs8.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs8.setVisibility(View.VISIBLE);
        }
        if (n < 8 || !oberstufe) {
            p2Kurs9.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs9.setVisibility(View.VISIBLE);
        }
        if (n < 9 || !oberstufe) {
            p2Kurs10.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs10.setVisibility(View.VISIBLE);
        }
        if (n < 10 || !oberstufe) {
            p2Kurs11.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs11.setVisibility(View.VISIBLE);
        }
        if (n < 11 || !oberstufe) {
            p2Kurs12.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs12.setVisibility(View.VISIBLE);
        }
        if (n < 12 || !oberstufe) {
            p2Kurs13.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs13.setVisibility(View.VISIBLE);
        }
        if (n < 13 || !oberstufe) {
            p2Kurs14.setVisibility(View.INVISIBLE);
        } else {
            p2Kurs14.setVisibility(View.VISIBLE);
        }
    }

    // Toast wenn Kurs gespeichert wird
    public void showKursToast(String name) {
        Toast.makeText(this, name + " gespeichert", Toast.LENGTH_SHORT).show();
    }

    // Speichere neuen Kurs
    public void saveNewKurs(String bearbeitet, Button but) {
        // falls unsinniger Kursname
        if (bearbeitet.equals("") || bearbeitet.equals("+")) {
            Toast.makeText(this, "falscher Kursname", Toast.LENGTH_SHORT).show();
        } else {
            // überprüfe auf Dopplungen
            if (doppelterKurs(bearbeitet)) {
                Toast.makeText(this, "Kurs bereits vorhanden", Toast.LENGTH_SHORT).show();
            } else {
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

                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                SharedPreferences.Editor editor4 = sharedPreferences4.edit();
                SharedPreferences.Editor editor5 = sharedPreferences5.edit();
                SharedPreferences.Editor editor6 = sharedPreferences6.edit();
                SharedPreferences.Editor editor7 = sharedPreferences7.edit();
                SharedPreferences.Editor editor8 = sharedPreferences8.edit();
                SharedPreferences.Editor editor9 = sharedPreferences9.edit();
                SharedPreferences.Editor editor10 = sharedPreferences10.edit();
                SharedPreferences.Editor editor11 = sharedPreferences11.edit();
                SharedPreferences.Editor editor12 = sharedPreferences12.edit();
                SharedPreferences.Editor editor13 = sharedPreferences13.edit();
                SharedPreferences.Editor editor14 = sharedPreferences14.edit();

                boolean maxReached = false;

                // Wurde lediglich ein bereits vorhandener Kurs bearbeitet, wird dieser neue Kurs an dessen Stelle gespeichert
                if (kursClicked > 0) {
                    but.setBackgroundResource(R.drawable.kurs_button);
                    if (kursClicked == 1) {
                        editor1.putString(kurs1, bearbeitet).apply();
                    } else if (kursClicked == 2) {
                        editor2.putString(kurs2, bearbeitet).apply();
                    } else if (kursClicked == 3) {
                        editor3.putString(kurs3, bearbeitet).apply();
                    } else if (kursClicked == 4) {
                        editor4.putString(kurs4, bearbeitet).apply();
                    } else if (kursClicked == 5) {
                        editor5.putString(kurs5, bearbeitet).apply();
                    } else if (kursClicked == 6) {
                        editor6.putString(kurs6, bearbeitet).apply();
                    } else if (kursClicked == 7) {
                        editor7.putString(kurs7, bearbeitet).apply();
                    } else if (kursClicked == 8) {
                        editor8.putString(kurs8, bearbeitet).apply();
                    } else if (kursClicked == 9) {
                        editor9.putString(kurs9, bearbeitet).apply();
                    } else if (kursClicked == 10) {
                        editor10.putString(kurs10, bearbeitet).apply();
                    } else if (kursClicked == 11) {
                        editor11.putString(kurs11, bearbeitet).apply();
                    } else if (kursClicked == 12) {
                        editor12.putString(kurs12, bearbeitet).apply();
                    } else if (kursClicked == 13) {
                        editor13.putString(kurs13, bearbeitet).apply();
                    } else if (kursClicked == 14) {
                        editor14.putString(kurs14, bearbeitet).apply();
                    }

                    // Bei der direkten Bearbeitung eines Kursnamen wird die Richtigkeit des Kurses nicht überprüft
                } else {
                    if (!wrongFormat) {
                        showKursToast(bearbeitet);
                    }

                    // Wird ein Kurs über den "Kurs Hinzufügen" Button eingegeben, wird dieser auf der niedrigsten leeren Stelle gespeichert
                    // Ist das Kursformat falsch, wird das Textfeld in Rot angezeigt, der Kurs aber vorerst trotzdem gespeichert
                    if (sharedPreferences1.getString(kurs1, "+").equals("+")) {
                        editor1.putString(kurs1, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs2);
                        if (wrongFormat) {
                            p2Kurs1.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs1.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 1;
                    } else if (sharedPreferences2.getString(kurs2, "+").equals("+")) {
                        editor2.putString(kurs2, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs1);
                        if (wrongFormat) {
                            p2Kurs2.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs2.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 2;
                    } else if (sharedPreferences3.getString(kurs3, "+").equals("+")) {
                        editor3.putString(kurs3, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs3);
                        if (wrongFormat) {
                            p2Kurs3.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs3.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 3;
                    } else if (sharedPreferences4.getString(kurs4, "+").equals("+")) {
                        editor4.putString(kurs4, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs4);
                        if (wrongFormat) {
                            p2Kurs4.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs4.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 4;
                    } else if (sharedPreferences5.getString(kurs5, "+").equals("+")) {
                        editor5.putString(kurs5, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs5);
                        if (wrongFormat) {
                            p2Kurs5.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs5.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 5;
                    } else if (sharedPreferences6.getString(kurs6, "+").equals("+")) {
                        editor6.putString(kurs6, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs6);
                        if (wrongFormat) {
                            p2Kurs6.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs6.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 6;
                    } else if (sharedPreferences7.getString(kurs7, "+").equals("+")) {
                        editor7.putString(kurs7, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs7);
                        if (wrongFormat) {
                            p2Kurs7.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs7.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 7;
                    } else if (sharedPreferences8.getString(kurs8, "+").equals("+")) {
                        editor8.putString(kurs8, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs8);
                        if (wrongFormat) {
                            p2Kurs8.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs8.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 8;
                    } else if (sharedPreferences9.getString(kurs9, "+").equals("+")) {
                        editor9.putString(kurs9, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs9);
                        if (wrongFormat) {
                            p2Kurs9.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs9.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 9;
                    } else if (sharedPreferences10.getString(kurs10, "+").equals("+")) {
                        editor10.putString(kurs10, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs10);
                        if (wrongFormat) {
                            p2Kurs10.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs10.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 10;
                    } else if (sharedPreferences11.getString(kurs11, "+").equals("+")) {
                        editor11.putString(kurs11, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs11);
                        if (wrongFormat) {
                            p2Kurs11.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs11.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 11;
                    } else if (sharedPreferences12.getString(kurs12, "+").equals("+")) {
                        editor12.putString(kurs12, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs12);
                        if (wrongFormat) {
                            p2Kurs12.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs12.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 12;
                    } else if (sharedPreferences13.getString(kurs13, "+").equals("+")) {
                        editor13.putString(kurs13, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs13);
                        if (wrongFormat) {
                            p2Kurs13.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs13.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 13;
                    } else if (sharedPreferences14.getString(kurs14, "+").equals("+")) {
                        editor14.putString(kurs14, bearbeitet).apply();
                        p2Kurs1.findViewById(R.id.p2Kurs14);
                        if (wrongFormat) {
                            p2Kurs14.setBackgroundResource(R.drawable.kurs_button_red);
                        } else {
                            p2Kurs14.setBackgroundResource(R.drawable.kurs_button);
                        }
                        kursClicked = 14;
                    } else {
                        // Falls bereits 14 Kurse gespeichert wurden
                        maxReached = true;
                        hideKeyboard();
                        Toast.makeText(this, "Maximale Anzahl erreicht" + "\n" + "Lösche erst einen Kurs", Toast.LENGTH_LONG).show();
                    }
                }

                // Bei einem falschen Kursnamen wird ein Popup aufgerufen, das den Nutzer darauf hinweist, und fragt, ob der Kurs gelöscht werden soll
                if (!maxReached && wrongFormat) {
                    callPopup = true;
                }
                updateViewsText();
                updateViewsVisibility();
            }
        }
    }

    // überprüft, ob eingegebener Kurs bereits existiert
    public boolean doppelterKurs(String bearbeitet) {
        boolean doppelt = false;

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


        if (sharedPreferences1.getString(kurs1, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences2.getString(kurs2, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences3.getString(kurs3, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences4.getString(kurs4, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences5.getString(kurs5, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences6.getString(kurs6, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences7.getString(kurs7, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences8.getString(kurs8, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences9.getString(kurs9, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences10.getString(kurs10, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences11.getString(kurs11, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences12.getString(kurs12, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences13.getString(kurs13, "+").equals(bearbeitet)) {
            doppelt = true;
        }
        if (sharedPreferences14.getString(kurs14, "+").equals(bearbeitet)) {
            doppelt = true;
        }

        return doppelt;
    }

    // Füllt Lücke, nachdem ein Kurs gelöscht wird, indem es von vorne nach hinten durchgeht und Kurse ggf. "aufrücken"
    public void resetKurse() {
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

        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
        SharedPreferences.Editor editor4 = sharedPreferences4.edit();
        SharedPreferences.Editor editor5 = sharedPreferences5.edit();
        SharedPreferences.Editor editor6 = sharedPreferences6.edit();
        SharedPreferences.Editor editor7 = sharedPreferences7.edit();
        SharedPreferences.Editor editor8 = sharedPreferences8.edit();
        SharedPreferences.Editor editor9 = sharedPreferences9.edit();
        SharedPreferences.Editor editor10 = sharedPreferences10.edit();
        SharedPreferences.Editor editor11 = sharedPreferences11.edit();
        SharedPreferences.Editor editor12 = sharedPreferences12.edit();
        SharedPreferences.Editor editor13 = sharedPreferences13.edit();
        SharedPreferences.Editor editor14 = sharedPreferences14.edit();

        if (sharedPreferences1.getString(kurs1, "+").equals("+")) {
            editor1.putString(kurs1, sharedPreferences2.getString(kurs2, "+")).apply();
            editor2.putString(kurs2, "+").apply();
        }
        if (sharedPreferences2.getString(kurs2, "+").equals("+")) {
            editor2.putString(kurs2, sharedPreferences3.getString(kurs3, "+")).apply();
            editor3.putString(kurs3, "+").apply();
        }
        if (sharedPreferences3.getString(kurs3, "+").equals("+")) {
            editor3.putString(kurs3, sharedPreferences4.getString(kurs4, "+")).apply();
            editor4.putString(kurs4, "+").apply();
        }
        if (sharedPreferences4.getString(kurs4, "+").equals("+")) {
            editor4.putString(kurs4, sharedPreferences5.getString(kurs5, "+")).apply();
            editor5.putString(kurs5, "+").apply();
        }
        if (sharedPreferences5.getString(kurs5, "+").equals("+")) {
            editor5.putString(kurs5, sharedPreferences6.getString(kurs6, "+")).apply();
            editor6.putString(kurs6, "+").apply();
        }
        if (sharedPreferences6.getString(kurs6, "+").equals("+")) {
            editor6.putString(kurs6, sharedPreferences7.getString(kurs7, "+")).apply();
            editor7.putString(kurs7, "+").apply();
        }
        if (sharedPreferences7.getString(kurs7, "+").equals("+")) {
            editor7.putString(kurs7, sharedPreferences8.getString(kurs8, "+")).apply();
            editor8.putString(kurs8, "+").apply();
        }
        if (sharedPreferences8.getString(kurs8, "+").equals("+")) {
            editor8.putString(kurs8, sharedPreferences9.getString(kurs9, "+")).apply();
            editor9.putString(kurs9, "+").apply();
        }
        if (sharedPreferences9.getString(kurs9, "+").equals("+")) {
            editor9.putString(kurs9, sharedPreferences10.getString(kurs10, "+")).apply();
            editor10.putString(kurs10, "+").apply();
        }
        if (sharedPreferences10.getString(kurs10, "+").equals("+")) {
            editor10.putString(kurs10, sharedPreferences11.getString(kurs11, "+")).apply();
            editor11.putString(kurs11, "+").apply();
        }
        if (sharedPreferences11.getString(kurs11, "+").equals("+")) {
            editor11.putString(kurs11, sharedPreferences12.getString(kurs12, "+")).apply();
            editor12.putString(kurs12, "+").apply();
        }
        if (sharedPreferences12.getString(kurs12, "+").equals("+")) {
            editor12.putString(kurs12, sharedPreferences13.getString(kurs13, "+")).apply();
            editor13.putString(kurs13, "+").apply();
        }
        if (sharedPreferences13.getString(kurs13, "+").equals("+")) {
            editor13.putString(kurs13, sharedPreferences14.getString(kurs14, "+")).apply();
            editor14.putString(kurs14, "+").apply();
        }

        updateViewsText();
        updateViewsVisibility();
    }

    //überprüft, ob bereits Kurse gespeichert wurden. Ist dies nicht der Fall, wird der Nutzer gebeten welche einzugeben
    public void checkIfKurseVorhanden() {
        // test for github
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS1, MODE_PRIVATE);
        if (sharedPreferences.getString(kurs1, "+").equals("+")) {
            p2AlleKurse.setText("Es wurden noch keine Kurse hinzugefügt");
        } else {
            p2AlleKurse.setText("Gespeicherte Kurse:");
        }
    }

    // Nutzer erhält Nachricht, falls in dem Textfeld etwas nicht gespeichertes stand bei dem Verlassen der Seite
    public void checkIfFieldsEmpty() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (p2Klasse.getText().toString().equals("#!#")) {
            Toast.makeText(this, "Wilkommen im Testmodus", Toast.LENGTH_SHORT).show();
        } else if (!p2Klasse.getText().toString().equals(sharedPreferences.getString(klasse, "---"))) {
            Toast.makeText(this, p2Klasse.getText().toString() + " nicht gespeichert", Toast.LENGTH_LONG).show();
        }
    }

    // Löscht jeweiligen Kurs und zeigt Toast an
    public void loeschen1() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS1, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs1, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
        checkIfKurseVorhanden();
    }

    public void loeschen2() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS2, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs2, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen3() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS3, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs3, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen4() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS4, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs4, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen5() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS5, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs5, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen6() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS6, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs6, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen7() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS7, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs7, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen8() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS8, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs8, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen9() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS9, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs9, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen10() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS10, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs10, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen11() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS11, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs11, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen12() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS12, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs12, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen13() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS13, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs13, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    public void loeschen14() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS14, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(kurs14, "+").apply();
        Toast.makeText(this, "Kurs gelöscht", Toast.LENGTH_SHORT).show();
        resetKurse();
    }

    // Falls jeweiliger Kurs angeklickt wird
    public void button1(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 1;
            kursPopup(view);
        }
    }

    public void button2(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 2;
            kursPopup(view);
        }
    }

    public void button3(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 3;
            kursPopup(view);
        }
    }

    public void button4(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 4;
            kursPopup(view);
        }
    }

    public void button5(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 5;
            kursPopup(view);
        }
    }

    public void button6(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 6;
            kursPopup(view);
        }
    }

    public void button7(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 7;
            kursPopup(view);
        }
    }

    public void button8(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 8;
            kursPopup(view);
        }
    }

    public void button9(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 9;
            kursPopup(view);
        }
    }

    public void button10(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 10;
            kursPopup(view);
        }
    }

    public void button11(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 11;
            kursPopup(view);
        }
    }

    public void button12(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 12;
            kursPopup(view);
        }
    }

    public void button13(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 13;
            kursPopup(view);
        }
    }

    public void button14(View view) {
        if(((TextView)view).getText().toString().equals("+")){
            kursHinzufuegen();
        } else {
            kursClicked = 14;
            kursPopup(view);
        }
    }

    /* Popup mit zwei Nutzen
     * 1. Fragt, ob ein Kurs im falschen Format gespeichert oder verworfen werden soll (falschesFormat == true)
     * 2. Löschen eines Kurses muss bestätigt oder abgebrochen werden (falschesFormat == false)
     */
    public void popupLoeschen(View but, boolean falschesFormat, String text) {
        hideKeyboard();

        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_kurse, null);
        findViews();
        // Unterscheidung zwischen zwei Formaten
        String message = "Soll " + text + " gelöscht werden?";
        if (falschesFormat) {
            message = "FALSCHES KURSFORMAT\nRichtige formate sind z.B. SW G1, D  G1, D  L1, BIB 1, GEZK 1\n" + message;
        }

        // Neues Popup Window wird Initialisiert
        popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        ((TextView) popupWindow2.getContentView().findViewById(R.id.p2PopupText)).setText(message);

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow2.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button p2PopupBehalten = popupView.findViewById(R.id.p2PopupBehalten);
        Button p2PopupLoeschen = popupView.findViewById(R.id.p2PopupLoeschen);

        // Soll der Kurs behalten werden, wird das fenster lediglich geschlossen
        p2PopupBehalten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
            }
        });

        // Bestimmter Kurs wird gelöscht
        p2PopupLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kursClicked == 1) {
                    loeschen1();
                } else if (kursClicked == 2) {
                    loeschen2();
                } else if (kursClicked == 3) {
                    loeschen3();
                } else if (kursClicked == 4) {
                    loeschen4();
                } else if (kursClicked == 5) {
                    loeschen5();
                } else if (kursClicked == 6) {
                    loeschen6();
                } else if (kursClicked == 7) {
                    loeschen7();
                } else if (kursClicked == 8) {
                    loeschen8();
                } else if (kursClicked == 9) {
                    loeschen9();
                } else if (kursClicked == 10) {
                    loeschen10();
                } else if (kursClicked == 11) {
                    loeschen11();
                } else if (kursClicked == 12) {
                    loeschen12();
                } else if (kursClicked == 13) {
                    loeschen13();
                } else if (kursClicked == 14) {
                    loeschen14();
                }
                kurseGeloescht++;
                popupWindow2.dismiss();
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // Falls der Nutzer bereits 3 Kurse hintereinander gelöscht hat, wird gefragt ob alle gelöscht werden sollen
    public void popupAlleLoeschen(final boolean first) {
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_kurse, null);

        popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);

        ((TextView) popupWindow2.getContentView().findViewById(R.id.p2PopupText)).setText("Sollen ALLE Kurse gelöscht werden?");
        ((Button) popupWindow2.getContentView().findViewById(R.id.p2PopupBehalten)).setText("Behalten");
        ((Button) popupWindow2.getContentView().findViewById(R.id.p2PopupLoeschen)).setText("Löschen");

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
                kurseGeloescht = 0;
                popupWindow2.dismiss();
            }
        });

        // Button, der zu der VP Website führt
        p2PopupLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
                if (first) {
                    popupAlleLoeschen(false);
                } else {
                    kurseGeloescht = 0;
                    loescheAlle();
                }
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // Alle Kurse werden gelöscht
    public void loescheAlle() {
        loeschen14();
        loeschen13();
        loeschen12();
        loeschen11();
        loeschen10();
        loeschen9();
        loeschen8();
        loeschen7();
        loeschen6();
        loeschen5();
        loeschen4();
        loeschen3();
        loeschen2();
        loeschen1();
    }

    // Ein neuer Kurs kann hinzugefügt werden
    public void kursHinzufuegen() {
        kursClicked = 0;
        popupKursBearbeiten(p2Kurs1);
    }

    /* Zwei Funktionen:
    1. Neuen Kurs eingeben
    2. Vorhandenen Kurs direkt bearbeiten
     */
    public void popupKursBearbeiten(View edit) {
        LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater2.inflate(R.layout.popup_kurse_aendern, null);

        popupWindow2 = new PopupWindow(this);
        popupWindow2.setContentView(popupView);
        popupWindow2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow2.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        // Popup wird 1/8 der Bildschirmgröße über dem Zentrum angezeigt, um nicht von der Tastatur verdeckt zu werden
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = Math.round(displayMetrics.heightPixels / 8);

        (findViewById(R.id.dimView)).setVisibility(View.VISIBLE);
        popupWindow2.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL, 0, -height);

        Button p2PopupAbbruch = popupView.findViewById(R.id.p2PopupAbbruch);
        Button p2PopupSpeichern = popupView.findViewById(R.id.p2PopupSpeichern);
        final TextView p2Kurs = popupView.findViewById(R.id.p2PopupKurs);
        TextView p2PopupText = popupView.findViewById(R.id.p2PopupText);

        // Unterscheidung zwischen Funktion 1 und 2
        if (kursClicked == 0) {
            p2PopupText.setText("Bitte gib einen Kurs ein!");
            //p2Kurs.setText("");
        } else {
            p2Kurs.setText(((Button) edit).getText().toString());
            p2PopupText.setText("Du kannst den Kursnamen nun bearbeiten!");
        }
        // Falls weniger als 5 Kurse bisher gespeichert wurden, wird das Popup immer wieder neu aufgrerufen
        if (!keepEingabe && p2Kurs7.getText().toString().equals("+")) {
            keepEingabe = true;
        }
        final Button but = (Button) edit;
        p2Kurs.requestFocus();
        showKeyboard();

        p2PopupAbbruch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Zeiger ist nicht mehr auf dem Eingabefeld
                p2Kurs.setSelected(false);
                keepEingabe = false;
                popupWindow2.dismiss();
            }
        });

        // Kurs wird entweder an einer bestimmten Stelle oder an der ersten freien Stelle gespeichert
        p2PopupSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastKursClicked = kursClicked;
                callPopup = false;
                String k = modifyKurs(p2Kurs.getText().toString());
                saveNewKurs(k, but);
                updateViewsText();
                updateViewsVisibility();
                checkIfKurseVorhanden();
                p2Kurs.setText("");
                popupWindow2.dismiss();
                // Falls der Kurs im falschen Format ist und es keine Bearbeitung war
                if (callPopup && lastKursClicked == 0) {
                    hideKeyboard();
                    popupLoeschen(but, true, k);
                } else if (keepEingabe) {
                    showKeyboard();
                    kursHinzufuegen();
                } else {
                    hideKeyboard();
                }
                updateScroller();
            }
        });

        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                (findViewById(R.id.dimView)).setVisibility(View.INVISIBLE);
            }
        });
    }

    // "Bearbeiten" und "Löschen" Buttons, die auftauchen, wenn ein Kurs angeklickt wird
    public void kursPopup(View yPoint) {
        final View but = yPoint;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.kurs_auswahl, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Buttons werden ein Stück über dem Kurs angezeigt
        int yOffset = (int) Math.round(2.15 * but.getHeight() + popupView.getMeasuredHeight());
        int xOffset = Math.round((popupView.getMeasuredHeight() - yPoint.getWidth()) / 2);
        popupWindow.showAsDropDown(yPoint, xOffset, -yOffset);

        final String kursText = ((Button)yPoint).getText().toString();
        Button p2bearbeiten = popupView.findViewById(R.id.p2PopupBearbeiten);
        Button p2Loeschen = popupView.findViewById(R.id.p2PopupLoeschenKlein);

        // Popup Window verschwindet, wenn der Bildschirm berührt wird
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        // Das Popup zum Bearbeiten eines Kurses wird aufgerufen
        p2bearbeiten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                popupKursBearbeiten(but);
            }
        });

        // Das Popup zur Bestätigung der Löschung eines Kurses wird aufgerufen
        p2Loeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (kurseGeloescht < 3) {
                    popupLoeschen(but, false, kursText);
                } else {
                    popupAlleLoeschen(true);
                }
                updateScroller();
            }
        });

    }

    // "Bearbeiten" und "Löschen" Buttons, die auftauchen, wenn ein Kurs angeklickt wird
    public void testPopup(View yPoint) {
        final View but = yPoint;

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.kurs_auswahl, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Buttons werden ein Stück über dem Kurs angezeigt
        int yOffset = (int) Math.round(2.15 * but.getHeight() + popupView.getMeasuredHeight());
        int xOffset = Math.round((popupView.getMeasuredHeight() - yPoint.getWidth()) / 2);
        popupWindow.showAsDropDown(yPoint, xOffset, -yOffset);

        Button p2bearbeiten = popupView.findViewById(R.id.p2PopupBearbeiten);
        Button p2Loeschen = popupView.findViewById(R.id.p2PopupLoeschenKlein);
        p2bearbeiten.setText("Zu\nIntro");
        p2Loeschen.setText("Test\nbeenden");

        // Popup Window verschwindet, wenn der Bildschirm berührt wird
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        // Das Popup zum Bearbeiten eines Kurses wird aufgerufen
        p2bearbeiten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS0, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(firstLaunch, "true").apply();
                zurueck(p2Klasse);
            }
        });

        // Das Popup zur Bestätigung der Löschung eines Kurses wird aufgerufen
        p2Loeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences(TEST, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(testMode, false).apply();
                zurueck(p2Klasse);
            }
        });
    }

    // Popup welches zur Eingabe einer neuen Klasse auftaucht
    public void popupKlasse(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_klasse, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        p2Klasse = findViewById(R.id.p2Klasse);
        // Buttons werden ein Stück über dem Kurs angezeigt
        int yOffset = p2Klasse.getHeight();
        popupWindow.showAsDropDown(p2Klasse, 0, -yOffset);

        Button p2KlasseSpeichern2 = popupView.findViewById(R.id.p2KlasseSpeichern2);
        final EditText p2Klasse2 = popupView.findViewById(R.id.p2Klasse2);
        p2Klasse2.requestFocus();
        showKeyboard();

        p2KlasseSpeichern2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p2Klasse2.requestFocus();
                hideKeyboard();
                SharedPreferences tst = getSharedPreferences(TEST, MODE_PRIVATE);
                if (p2Klasse2.getText().toString().equals("#!#")) {
                    SharedPreferences.Editor editor = tst.edit();
                    editor.putBoolean(testMode, true).apply();
                    zurueck(p2Klasse);
                } else {
                    saveKlasse(p2Klasse2.getText().toString());
                    updateViewsText();
                    updateViewsVisibility();
                }
                popupWindow.dismiss();
                SharedPreferences sharedPreferences1 = getSharedPreferences(PREFS1, MODE_PRIVATE);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
                String kurs = sharedPreferences.getString(klasse, "---");
                if ((kurs.equals("EF") || kurs.equals("Q1") || kurs.equals("Q2")) && sharedPreferences1.getString(kurs1, "+").equals("+")) {
                    kursHinzufuegen();
                }
                updateScroller();
            }
        });

    }

    // Menü popup
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
                zurueck(profil);
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
                System.out.println("clicked");
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

    // Button zurück zur Hauptseite
    public void zurueck(View view) {
        hideKeyboard();
        checkIfFieldsEmpty();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    // Alle folgenden Methoden sind großteils identisch zu den in der MainActivity
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        RadioButton radioImmer = popupWindow2.getContentView().findViewById(R.id.radioImmer);
        RadioButton radioWlan = popupWindow2.getContentView().findViewById(R.id.radioWlan);
        RadioButton radioNie = popupWindow2.getContentView().findViewById(R.id.radioNie);

        SharedPreferences sharedPrefs = getSharedPreferences(NETWORK, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        // Check which radio button was clicked
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

    public void scheduleJob(View view){
        cancelJob(view);
        ComponentName comp = new ComponentName(this, BackgroundJob.class);
        // any = 0, unmetered = 1, no Job = 2
        JobInfo info;
        if(view.getId() == R.id.radioImmer){
            info = new JobInfo.Builder(repeatedJobId, comp)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(1000*60*30)
                    .setPersisted(true)
                    .build();
        } else {
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

    public void cancelJob(View view){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if(scheduler != null) {
            scheduler.cancel(repeatedJobId);
        }
        Log.d(Tag, "Job cancelled");
    }

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

    public void cancelDaily(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), DailyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), dailyJobId, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    public void handleTime(){
        SharedPreferences prefs = getSharedPreferences(TIME, MODE_PRIVATE);
        if(prefs.getString(dailyTime, "off").equals("off")){
            popupTime();
        } else {
            popupCancelTime();
        }
    }

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

        // call input for time
    }

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

    public void setDarkModeVisible(PopupWindow popup){
        ImageView darkModeOn = popup.getContentView().findViewById(R.id.darkModeOn);
        ImageView darkModeOff = popup.getContentView().findViewById(R.id.darkModeOff);

        darkModeOn.setVisibility(View.GONE);
        darkModeOff.setVisibility(View.GONE);

        System.out.println("checking");
        if(switchesInvisible) {
            System.out.println("both off");
            SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
            boolean dark = prefs.getBoolean(darkMode, false);
            if (dark){
                darkModeOn.setVisibility(View.VISIBLE);
            } else {
                darkModeOff.setVisibility(View.VISIBLE);
            }
            switchesInvisible = false;
        } else {
            switchesInvisible = true;
        }
    }

    public void setDarkMode(PopupWindow popup, View v){
        boolean dark = false;
        if(v.getId() == R.id.darkModeOff){
            dark = true;
        }
        SharedPreferences prefs = getSharedPreferences(DARKMODE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(darkMode, dark).apply();

        System.out.println(dark);
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