package com.example.azil.Models;

public class Shelter_Donation {
    private String id;
    private String donacija;
    private String skloniste;

    public Shelter_Donation() {
    }

    public Shelter_Donation(String id, String donacija, String skloniste) {
        this.id = id;
        this.donacija = donacija;
        this.skloniste = skloniste;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDonacija() {
        return donacija;
    }

    public void setDonacija(String donacija) {
        this.donacija = donacija;
    }

    public String getSkloniste() {
        return skloniste;
    }

    public void setSkloniste(String skloniste) {
        this.skloniste = skloniste;
    }
}
