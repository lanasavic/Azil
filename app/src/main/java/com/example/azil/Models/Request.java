package com.example.azil.Models;

public class Request {
    private String ime_prezime;
    private String email;
    private String komentar;
    private String datum;
    private String sifra;

    public Request() {
    }

    public Request(String ime_prezime, String email, String komentar, String datum, String sifra) {
        this.ime_prezime = ime_prezime;
        this.email = email;
        this.komentar = komentar;
        this.datum = datum;
        this.sifra = sifra;
    }

    public String getIme_prezime() {
        return ime_prezime;
    }

    public void setIme_prezime(String ime_prezime) {
        this.ime_prezime = ime_prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }
}
