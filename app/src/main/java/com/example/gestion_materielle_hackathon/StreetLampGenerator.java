package com.example.gestion_materielle_hackathon;

import com.example.gestion_materielle_hackathon.model.StreetLamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class StreetLampGenerator {

    private static final double EARTH_RADIUS = 6371e3; // Earth radius in meters

    // Function to generate random ID
    private static String generateRandomId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder id = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            id.append(characters.charAt(random.nextInt(characters.length())));
        }
        return id.toString();
    }

    // Function to generate street lamp coordinates along streets
    public static List<StreetLamp> generateStreetLampsAlongStreets(List<StreetSegment> streets, double distance) {
        List<StreetLamp> lamps = new ArrayList<>();
        Random random = new Random();

        for (StreetSegment street : streets) {
            List<Coordinate> coordinates = street.getCoordinates();
            double accumulatedDistance = 0;

            for (int i = 0; i < coordinates.size() - 1; i++) {
                Coordinate start = coordinates.get(i);
                Coordinate end = coordinates.get(i + 1);
                double segmentDistance = calculateDistance(start, end);
                accumulatedDistance += segmentDistance;

                // Generate street lamp with random on/off state
                boolean isOn = random.nextDouble() >= 0.02; // 2% off, 98% on
                lamps.add(new StreetLamp(generateRandomId(8), end.getLat(), end.getLon(), isOn));

                accumulatedDistance = 0;
            }
        }
        return lamps;
    }

    // Calculate distance between two coordinates
    private static double calculateDistance(Coordinate start, Coordinate end) {
        double lat1 = Math.toRadians(start.getLat());
        double lon1 = Math.toRadians(start.getLon());
        double lat2 = Math.toRadians(end.getLat());
        double lon2 = Math.toRadians(end.getLon());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}