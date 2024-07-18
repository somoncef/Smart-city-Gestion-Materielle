package com.example.gestion_materielle_hackathon;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.gestion_materielle_hackathon.model.Lamp;
import com.example.gestion_materielle_hackathon.model.StreetLamp;
import com.google.android.gms.maps.model.BitmapDescriptor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class Supervision extends Fragment  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    Button bOn, bOff,bAllLamps;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference myRef;

    private Query lampQuery;
    private String lastLampId = null;
    private BitmapDescriptor lampOnIcon;
    private BitmapDescriptor lampOffIcon;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        bOff = view.findViewById(R.id.bOff);
        bOn = view.findViewById(R.id.bOn);
        bAllLamps = view.findViewById(R.id.bAllLamps);

        bOn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Showing off lamps", Toast.LENGTH_SHORT).show();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear(); // Clear all markers
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Lamp lamp = postSnapshot.getValue(Lamp.class);
                        assert lamp != null;
                        if (lamp.isOn()) { // Check if the lamp is off
                            LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(lampLocation)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.lampon))
                                    .title(lamp.getId())
                                    .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude());
                            mMap.addMarker(markerOptions); // Add marker for the off lamp
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "", error.toException());
                }
            });
        });

        bOff.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Showing off lamps", Toast.LENGTH_SHORT).show();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear(); // Clear all markers
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        Lamp lamp = postSnapshot.getValue(Lamp.class);
                        assert lamp != null;
                        if (!lamp.isOn()) { // Check if the lamp is off
                            LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(lampLocation)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.lampoff))
                                    .title(lamp.getId())
                                    .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude());
                            mMap.addMarker(markerOptions); // Add marker for the off lamp
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "", error.toException());
                }
            });
        });

        bAllLamps.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Clearing all lamps", Toast.LENGTH_SHORT).show();
            mMap.clear(); // Clear all markers
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the permission is not granted, request the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("lamps");
        
//        fetchStreetDataAndGenerateLamps();


    }

    private void fetchStreetDataAndGenerateLamps() {
        new Thread(() -> {
            try {
                OSMStreetDataFetcher fetcher = new OSMStreetDataFetcher();
                double minLat = 35.604570;
                double minLon =  -5.294401;
                double maxLat = 35.643955;
                double maxLon = -5.269397;
                List<StreetSegment> streets = fetcher.fetchStreetData(minLat, minLon, maxLat, maxLon);

                double averageDistance = 50;  // distance between street lamps in meters
                List<StreetLamp> streetLamps = StreetLampGenerator.generateStreetLampsAlongStreets(streets, averageDistance);

                // Save the generated street lamp coordinates to Firebase
                saveStreetLampsToFirebase(streetLamps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveStreetLampsToFirebase(List<StreetLamp> streetLamps) {
        for (StreetLamp lamp : streetLamps) {
            String lampKey = myRef.push().getKey();
            if (lampKey != null) {
                myRef.child(lampKey).setValue(lamp);
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervision, container, false);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        lampOnIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampon);
        lampOffIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampoff);

        // Rest
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;  // Use the default InfoWindow frame
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);

                TextView tvMapId = view.findViewById(R.id.tvMapId);
                TextView tvMapLatitude = view.findViewById(R.id.tvMapLatitude);
                TextView tvMapLongitude = view.findViewById(R.id.tvMapLongitude);

                tvMapId.setText(marker.getTitle());
                String[] snippets = Objects.requireNonNull(marker.getSnippet()).split("\n");
                tvMapLatitude.setText(snippets[0]);
                tvMapLongitude.setText(snippets[1]);

                return view;
            }
        });

        LatLng targetLocation = new LatLng(35.6234197, -5.2740685);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 19));

        BitmapDescriptor lampOnIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampon);
        BitmapDescriptor lampOffIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampoff);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lamp lamp = postSnapshot.getValue(Lamp.class);
                    assert lamp != null;
                    LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(lampLocation) .icon(lamp.isOn() ? lampOnIcon : lampOffIcon).title(lamp.getId()).snippet(lamp.getLatitude() + "\n" + lamp.getLongitude());

                    /*MarkerOptions markerOptions = new MarkerOptions()
                            .position(lampLocation)
                            .icon(lamp.isOn() ? lampOnIcon : lampOffIcon)
                            .title(lamp.getId()).snippet(String.valueOf(lamp.getLongitude()) );*/
                    mMap.addMarker(markerOptions);
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "", error.toException());
            }
        });
        lampQuery = myRef.orderByKey().limitToFirst(50);
        loadLamps();
    }
    private void loadLamps() {
        lampQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Lamp lamp = postSnapshot.getValue(Lamp.class);
                    assert lamp != null;
                    LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(lampLocation) .icon(lamp.isOn() ? lampOnIcon : lampOffIcon).title(lamp.getId()).snippet(lamp.getLatitude() + "\n" + lamp.getLongitude());
                    mMap.addMarker(markerOptions);

                    // Update the last lamp ID
                    lastLampId = postSnapshot.getKey();
                }

                // Update the query for the next load
                lampQuery = myRef.orderByKey().startAfter(lastLampId).limitToFirst(50);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "", error.toException());
            }
        });
    }
}