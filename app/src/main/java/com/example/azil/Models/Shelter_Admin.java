package com.example.azil.Models;

public class Shelter_Admin {
    private String admin;
    private String skloniste;
    private String id;

    public Shelter_Admin() {
    }

    public Shelter_Admin(String admin, String skloniste, String id) {
        this.admin = admin;
        this.skloniste = skloniste;
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getSkloniste() {
        return skloniste;
    }

    public void setSkloniste(String skloniste) {
        this.skloniste = skloniste;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}