package com.example.azil.Models;

import java.io.Serializable;

public class Shelter implements Serializable {
    private String naziv;
    private String adresa;
    private String grad;
    private String oib;
    private String iban;
    private String dostupnih_mjesta;

    public Shelter() {
    }

    public Shelter(String naziv, String adresa, String grad, String oib, String iban, String dostupnih_mjesta) {
        this.naziv = naziv;
        this.adresa = adresa;
        this.grad = grad;
        this.oib = oib;
        this.iban = iban;
        this.dostupnih_mjesta = dostupnih_mjesta;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
    }

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getDostupnih_mjesta() {
        return dostupnih_mjesta;
    }

    public void setDostupnih_mjesta(String dostupnih_mjesta) {
        this.dostupnih_mjesta = dostupnih_mjesta;
    }
}