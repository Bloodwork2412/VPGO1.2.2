/*
 * Created by Noel Billing in 2019
 * Copyright (c)  08/14/2019. All rights reserved.
 */

// Inhaltstyp der Arraylists zum Speichern von Ereignissen

package com.example.vp_go;

public class Datapoint {

    private String art;
    private String stunde;
    private String fach;
    private String lehrer;
    private String klasse;
    private String raum;
    private String notiz;
    private String tag;

    public Datapoint(String artx, String stundex, String fachx, String lehrerx, String klassex, String raumx, String notizx, String tagx){
        art = artx;
        stunde = stundex;
        fach = fachx;
        lehrer = lehrerx;
        klasse = klassex;
        raum = raumx;
        notiz = notizx;
        tag = tagx;
    }

    public String getArt() {
        return art;
    }

    public String getStunde() {
        return stunde;
    }

    public String getFach() {
        return fach;
    }

    public String getLehrer() {
        return lehrer;
    }

    public String getKlasse() {
        return klasse;
    }

    public String getRaum() {
        return raum;
    }

    public String getNotiz() {
        return notiz;
    }

    public String getTag() {
        return tag;
    }
}
