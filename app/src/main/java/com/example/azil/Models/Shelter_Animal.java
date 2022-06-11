package com.example.azil.Models;

public class Shelter_Animal {
    private String zivotinja;
    private String skloniste;
    private String id;

    public Shelter_Animal() {
    }

    public Shelter_Animal(String zivotinja, String skloniste, String id) {
        this.zivotinja = zivotinja;
        this.skloniste = skloniste;
        this.id = id;
    }

    public String getZivotinja() {
        return zivotinja;
    }

    public void setZivotinja(String zivotinja) {
        this.zivotinja = zivotinja;
    }

    public String getSkloniste() {
        return skloniste;
    }

    public void setSkloniste(String skloniste) {
        this.skloniste = skloniste;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}