package com.example.azil.Models;

public class ReceivedDonation {
    private String ime_prezime;
    private String email;
    private String kolicina;
    private String komentar;
    private String sifra;

    public ReceivedDonation() {
    }

    public ReceivedDonation(String ime_prezime, String email, String kolicina, String komentar, String sifra) {
        this.ime_prezime = ime_prezime;
        this.email = email;
        this.kolicina = kolicina;
        this.komentar = komentar;
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

    public String getKolicina() {
        return kolicina;
    }

    public void setKolicina(String kolicina) {
        this.kolicina = kolicina;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }
}
