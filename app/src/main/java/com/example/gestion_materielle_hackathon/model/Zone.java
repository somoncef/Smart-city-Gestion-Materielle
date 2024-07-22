package com.example.gestion_materielle_hackathon.model;

import com.example.gestion_materielle_hackathon.Coordinate;

public class Zone {
    private Coordinate topLeft;
    private Coordinate topRight;
    private Coordinate bottomLeft;
    private Coordinate bottomRight;
    private String priority;
    private String name;


    public Zone(Coordinate topLeft, Coordinate topRight, Coordinate bottomLeft, Coordinate bottomRight, String priority , String name) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.priority = priority;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getTopLeft() {
        return topLeft;
    }

    public Coordinate getTopRight() {
        return topRight;
    }

    public Coordinate getBottomLeft() {
        return bottomLeft;
    }

    public Coordinate getBottomRight() {
        return bottomRight;
    }

    // Check if a coordinate is within the zone
    public boolean contains(Coordinate coord) {
        // Check if the latitude is between the top and bottom boundaries
        boolean isLatInRange = Math.min(topLeft.getLat(), bottomLeft.getLat()) <= coord.getLat() && coord.getLat() <= Math.max(topLeft.getLat(), bottomLeft.getLat());
        // Check if the longitude is between the left and right boundaries
        boolean isLonInRange = Math.min(topLeft.getLon(), topRight.getLon()) <= coord.getLon() && coord.getLon() <= Math.max(topLeft.getLon(), topRight.getLon());
        return isLatInRange && isLonInRange;
    }
    public String getPriority() {
        return priority;
    }
}