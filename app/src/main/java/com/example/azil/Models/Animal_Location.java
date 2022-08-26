package com.example.azil.Models;

public class Animal_Location {
    private String id;
    private String zivotinja;
    private String lokacija;

    public Animal_Location() {
    }

    public Animal_Location(String id, String zivotinja, String lokacija) {
        this.id = id;
        this.zivotinja = zivotinja;
        this.lokacija = lokacija;
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

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }
}
