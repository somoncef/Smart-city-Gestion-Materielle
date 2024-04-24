package com.example.gestion_materielle_hackathon.model;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.BitmapDescriptor;

public class LampMarkerOptions extends MarkerOptions {
    private String id;
    private String lampType;

    public LampMarkerOptions() {
        super();
    }

    public LampMarkerOptions position(LatLng position) {
        super.position(position);
        return this;
    }

    public LampMarkerOptions icon(BitmapDescriptor icon) {
        super.icon(icon);
        return this;
    }

    public LampMarkerOptions id(String id) {
        this.id = id;
        return this;
    }

    public LampMarkerOptions lampType(String lampType) {
        this.lampType = lampType;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getLampType() {
        return lampType;
    }
}
