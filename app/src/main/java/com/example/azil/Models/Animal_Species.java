package com.example.azil.Models;

public class Animal_Species {
    private String id;
    private String zivotinja;
    private String vrsta;

    public Animal_Species() {
    }

    public Animal_Species(String id, String zivotinja, String vrsta) {
        this.id = id;
        this.zivotinja = zivotinja;
        this.vrsta = vrsta;
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

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }
}
