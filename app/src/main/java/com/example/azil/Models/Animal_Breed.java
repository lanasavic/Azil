package com.example.azil.Models;

public class Animal_Breed {
    private String id;
    private String zivotinja;
    private String pasmina;

    public Animal_Breed() {
    }

    public Animal_Breed(String id, String zivotinja, String pasmina) {
        this.id = id;
        this.zivotinja = zivotinja;
        this.pasmina = pasmina;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZivotinja() {
        return zivotinja;
    }

    public void setZivotinja(String zivotinja) {
        this.zivotinja = zivotinja;
    }

    public String getPasmina() {
        return pasmina;
    }

    public void setPasmina(String pasmina) {
        this.pasmina = pasmina;
    }
}
