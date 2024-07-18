package com.example.gestion_materielle_hackathon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class OSMStreetDataFetcher {

    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public List<StreetSegment> fetchStreetData(double minLat, double minLon, double maxLat, double maxLon) throws IOException {
        String query = String.format(
                "[out:json];way[highway](%f,%f,%f,%f);out geom;",
                minLat, minLon, maxLat, maxLon
        );

        Request request = new Request.Builder()
                .url(OVERPASS_API_URL + "?data=" + query)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);
            JsonArray elements = json.getAsJsonArray("elements");
            List<StreetSegment> streets = new ArrayList<>();

            for (JsonElement element : elements) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.get("type").getAsString().equals("way")) {
                    JsonArray geometry = obj.getAsJsonArray("geometry");
                    List<Coordinate> coordinates = new ArrayList<>();
                    for (JsonElement geom : geometry) {
                        JsonObject point = geom.getAsJsonObject();
                        coordinates.add(new Coordinate(
                                point.get("lat").getAsDouble(),
                                point.get("lon").getAsDouble()
                        ));
                    }
                    streets.add(new StreetSegment(coordinates));
                }
            }

            return streets;
        }
    }
}