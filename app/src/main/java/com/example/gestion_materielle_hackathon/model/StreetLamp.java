package com.example.gestion_materielle_hackathon.model;

public class StreetLamp {
    private String id;
    private double latitude;
    private double longitude;
    private boolean on;

    public StreetLamp(String id, double latitude, double longitude, boolean on) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.on = on;
    }

    public StreetLamp() {
    }

    public StreetLamp(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.on = false;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}