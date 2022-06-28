package com.example.azil.Models;

public class ReceivedDonation {
    private String email;
    private String kolicina;
    private String komentar;
    private String sifra;

    public ReceivedDonation() {
    }

    public ReceivedDonation(String email, String kolicina, String komentar, String sifra) {
        this.email = email;
        this.kolicina = kolicina;
        this.komentar = komentar;
        this.sifra = sifra;
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
