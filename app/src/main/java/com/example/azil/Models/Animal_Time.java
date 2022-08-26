package com.example.azil.Models;

public class Animal_Time {
    private String id;
    private String zivotinja;
    private String mjesec;

    public Animal_Time() {
    }

    public Animal_Time(String id, String zivotinja, String mjesec) {
        this.id = id;
        this.zivotinja = zivotinja;
        this.mjesec = mjesec;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZivotinja() {
        return zivotinja;
    }

    public void setZivotinja(String zivotinja) {
        this.zivotinja = zivotinja;
    }

    public String getMjesec() {
        return mjesec;
    }

    public void setMjesec(String mjesec) {
        this.mjesec = mjesec;
    }
}
