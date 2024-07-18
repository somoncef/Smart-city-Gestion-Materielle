package com.example.gestion_materielle_hackathon;

import java.util.List;

public class StreetSegment {
    private final List<Coordinate> coordinates;

    public StreetSegment(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
}