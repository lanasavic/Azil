package com.example.azil.Models;

public class Requested_Received {
    private String id;
    private String trazeno;
    private String zaprimljeno;

    public Requested_Received() {
    }

    public Requested_Received(String id, String trazeno, String zaprimljeno) {
        this.id = id;
        this.trazeno = trazeno;
        this.zaprimljeno = zaprimljeno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrazeno() {
        return trazeno;
    }

    public void setTrazeno(String trazeno) {
        this.trazeno = trazeno;
    }

    public String getZaprimljeno() {
        return zaprimljeno;
    }

    public void setZaprimljeno(String zaprimljeno) {
        this.zaprimljeno = zaprimljeno;
    }
}
