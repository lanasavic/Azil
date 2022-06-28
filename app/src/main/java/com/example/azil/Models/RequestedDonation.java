package com.example.azil.Models;

public class RequestedDonation {
    private String kolicina;
    private String opis;
    private String sifra;

    public RequestedDonation() {
    }

    public RequestedDonation(String kolicina, String opis, String sifra) {
        this.kolicina = kolicina;
        this.opis = opis;
        this.sifra = sifra;
    }

    public String getKolicina() {
        return kolicina;
    }

    public void setKolicina(String kolicina) {
        this.kolicina = kolicina;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }
}
