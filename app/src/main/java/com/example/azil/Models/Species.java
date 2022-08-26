package com.example.azil.Models;

public class Species {
    private String sifra;
    private String naziv;
    private String broj_zivotinja;

    public Species() {
    }

    public Species(String sifra, String naziv, String broj_zivotinja) {
        this.sifra = sifra;
        this.naziv = naziv;
        this.broj_zivotinja = broj_zivotinja;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getBroj_zivotinja() {
        return broj_zivotinja;
    }

    public void setBroj_zivotinja(String broj_zivotinja) {
        this.broj_zivotinja = broj_zivotinja;
    }
}