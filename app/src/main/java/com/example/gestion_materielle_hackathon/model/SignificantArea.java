package com.example.gestion_materielle_hackathon.model;

import com.example.gestion_materielle_hackathon.Coordinate;

import java.util.List;

public class SignificantArea {
    private List<Coordinate> coordinates;

    public SignificantArea(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
}
