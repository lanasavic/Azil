package com.example.azil.Models;

public class Animal_Request {
    private String id;
    private String zahtjev;
    private String zivotinja;

    public Animal_Request() {
    }

    public Animal_Request(String id, String zahtjev, String zivotinja) {
        this.id = id;
        this.zahtjev = zahtjev;
        this.zivotinja = zivotinja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZahtjev() {
        return zahtjev;
    }

    public void setZahtjev(String zahtjev) {
        this.zahtjev = zahtjev;
    }

    public String getZivotinja() {
        return zivotinja;
    }

    public void setZivotinja(String zivotinja) {
        this.zivotinja = zivotinja;
    }
}