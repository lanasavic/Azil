package com.example.azil.Models;

import java.io.Serializable;

public class Animal implements Serializable {
    private String ime;
    private String opis;
    private String sifra;
    private String imgurl;

    public Animal() {
    }

    public Animal(String ime, String opis, String sifra, String imgurl) {
        this.ime = ime;
        this.opis = opis;
        this.sifra = sifra;
        this.imgurl = imgurl;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
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

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
