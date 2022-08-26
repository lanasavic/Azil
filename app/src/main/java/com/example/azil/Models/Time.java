package com.example.azil.Models;

public class Time {
    private String sifra;
    private String mjesec;
    private String broj_zivotinja;

    public Time() {
    }

    public Time(String sifra, String mjesec, String broj_zivotinja) {
        this.sifra = sifra;
        this.mjesec = mjesec;
        this.broj_zivotinja = broj_zivotinja;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getMjesec() {
        return mjesec;
    }

    public void setMjesec(String mjesec) {
        this.mjesec = mjesec;
    }

    public String getBroj_zivotinja() {
        return broj_zivotinja;
    }

    public void setBroj_zivotinja(String broj_zivotinja) {
        this.broj_zivotinja = broj_zivotinja;
    }
}
