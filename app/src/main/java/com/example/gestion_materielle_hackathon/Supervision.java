package com.example.gestion_materielle_hackathon;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.gestion_materielle_hackathon.model.Lamp;
import com.example.gestion_materielle_hackathon.model.StreetLamp;
import com.example.gestion_materielle_hackathon.model.Zone;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Supervision extends Fragment  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    Button bOn, bOff,bAllLamps;
    private GoogleMap mMap;
    private DatabaseReference equipesRef,lampsRef;

    private Query lampQuery;
    private String lastLampId = null;
    private BitmapDescriptor lampOnIcon;
    private BitmapDescriptor lampOffIcon;
    private FirebaseAuth fAuth;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lampsRef = database.getReference("lamps");
         equipesRef = database.getReference("equipes");

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        bOff = view.findViewById(R.id.bOff);
        bOn = view.findViewById(R.id.bOn);
        bAllLamps = view.findViewById(R.id.bAllLamps);

        fetchLampsOnZone();

        bOff.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Showing off lamps", Toast.LENGTH_SHORT).show();
            equipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String assignedZone = null;
                    for (DataSnapshot equipeSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot chefSnapshot = equipeSnapshot.child("chef");
                        if (Objects.equals(chefSnapshot.child("id").getValue(String.class), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            assignedZone = chefSnapshot.child("assignedZone").getValue(String.class);
                            break;
                        }
                    }

                    if (assignedZone != null) {
                        final String finalAssignedZone = assignedZone;
                        lampsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mMap.clear(); // Clear all markers
                                for (DataSnapshot lampSnapshot : dataSnapshot.getChildren()) {
                                    if (finalAssignedZone.equals(lampSnapshot.child("zoneName").getValue(String.class))) {
                                        StreetLamp lamp = lampSnapshot.getValue(StreetLamp.class);
                                        assert lamp != null;
                                        if (!lamp.isOn()) { // Check if the lamp is off
                                            LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(lampLocation)
                                                    .icon(lampOffIcon)
                                                    .title(lamp.getId())
                                                    .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude() + "\n" + lamp.getPriority() + "\n" + lamp.getZoneName()); // Fetch the priority here
                                            mMap.addMarker(markerOptions);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("Error reading lamps: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "No assigned zone found for you", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error reading equipes: " + databaseError.getMessage());
                }
            });
        });
        bOn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Showing off lamps", Toast.LENGTH_SHORT).show();
            equipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String assignedZone = null;
                    for (DataSnapshot equipeSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot chefSnapshot = equipeSnapshot.child("chef");
                        if (Objects.equals(chefSnapshot.child("id").getValue(String.class), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            assignedZone = chefSnapshot.child("assignedZone").getValue(String.class);
                            break;
                        }
                    }

                    if (assignedZone != null) {
                        final String finalAssignedZone = assignedZone;
                        lampsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mMap.clear(); // Clear all markers
                                for (DataSnapshot lampSnapshot : dataSnapshot.getChildren()) {
                                    if (finalAssignedZone.equals(lampSnapshot.child("zoneName").getValue(String.class))) {
                                        StreetLamp lamp = lampSnapshot.getValue(StreetLamp.class);
                                        assert lamp != null;
                                        if (lamp.isOn()) { // Check if the lamp is off
                                            LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(lampLocation)
                                                    .icon(lampOnIcon)
                                                    .title(lamp.getId())
                                                    .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude() + "\n" + lamp.getPriority() + "\n" + lamp.getZoneName()); // Fetch the priority here
                                            mMap.addMarker(markerOptions);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("Error reading lamps: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "No assigned zone found for you", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error reading equipes: " + databaseError.getMessage());
                }
            });
        });

        bAllLamps.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Loading all lamps", Toast.LENGTH_SHORT).show();
                fetchLampsOnZone();
            });


        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }



        
//        fetchStreetDataAndGenerateLamps();


    }

    private void fetchLampsOnZone() {

        equipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String assignedZone = null;
                for (DataSnapshot equipeSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot chefSnapshot = equipeSnapshot.child("chef");
                    if (Objects.equals(chefSnapshot.child("id").getValue(String.class), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                        assignedZone = chefSnapshot.child("assignedZone").getValue(String.class);
                        break;
                    }
                }

                if (assignedZone != null) {
                    final String finalAssignedZone = assignedZone;
                    lampsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot lampSnapshot : dataSnapshot.getChildren()) {
                                if (finalAssignedZone.equals(lampSnapshot.child("zoneName").getValue(String.class))) {
                                    StreetLamp lamp = lampSnapshot.getValue(StreetLamp.class);
                                    assert lamp != null;
                                    LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(lampLocation)
                                            .icon(lamp.isOn() ? lampOnIcon : lampOffIcon)
                                            .title(lamp.getId())
                                            .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude() + "\n" + lamp.getPriority() + "\n" + lamp.getZoneName()); // Fetch the priority here
                                    mMap.addMarker(markerOptions);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("Error reading lamps: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "No assigned zone found for you", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error reading equipes: " + databaseError.getMessage());
            }
        });
    }

    private void fetchStreetDataAndGenerateLamps() {
        new Thread(() -> {
            try {
                OSMStreetDataFetcher fetcher = new OSMStreetDataFetcher();
                double minLat = 35.59894;
                double minLon =  -5.31188;
                double maxLat = 35.64271;
                double maxLon = -5.26630;
                List<StreetSegment> streets = fetcher.fetchStreetData(minLat, minLon, maxLat, maxLon);

                // Define zones with their boundaries and priorities
                List<Zone> zones = Arrays.asList(
                        new Zone(new Coordinate(35.63526, -5.31188), new Coordinate(35.63774, -5.30080), new Coordinate(35.62315, -5.30778), new Coordinate(35.62564, -5.29669), "low" , "zone 1"),
                        new Zone(new Coordinate(35.63774, -530080), new Coordinate(35.64023, -5.28971), new Coordinate(35.62564, -5.29669), new Coordinate(35.62812, -5.28560), "medium", "zone 2"),
                        new Zone(new Coordinate(35.64023, -5.28971), new Coordinate(35.64271, -5.27862), new Coordinate(35.62812, -5.28560), new Coordinate(35.63061, -5.27452), "low", "zone 3"),
                        new Zone(new Coordinate(35.62315, -5.30778), new Coordinate(35.62564, -5.29669), new Coordinate(35.61105, -5.30367), new Coordinate(35.61353, -5.29258), "high", "zone 4"),
                        new Zone(new Coordinate(35.62564, -5.29669), new Coordinate(35.62812, -5.28560), new Coordinate(35.61353, -5.29258), new Coordinate(35.61602, -5.28150), "high", "zone 5"),
                        new Zone(new Coordinate(35.62812, -5.28560), new Coordinate(35.63061, -5.27452), new Coordinate(35.61602, -5.28150), new Coordinate(35.61850, -5.27041), "high", "zone 6"),
                        new Zone(new Coordinate(35.61105, -5.30367), new Coordinate(35.61353, -5.29258), new Coordinate(35.59894, -5.29957), new Coordinate(35.660142, -5.28844), "low", "zone 7"),
                        new Zone(new Coordinate(35.61353, -5.29258), new Coordinate(35.61602, -5.28150), new Coordinate(35.660142, -5.28844), new Coordinate(35.60391, -5.27739), "medium", "zone 8"),
                        new Zone(new Coordinate(35.61602, -5.28150), new Coordinate(35.61850, -5.27041), new Coordinate(35.60391, -5.27739), new Coordinate(35.60639, -5.26630), "high", "zone 9")
                );

                double averageDistance = 50;  // distance between street lamps
                List<StreetLamp> lamps = StreetLampGenerator.generateStreetLampsAlongStreets(streets, averageDistance, zones);

                // Print the generated street lamps
                for (StreetLamp lamp : lamps) {
                    System.out.println("ID: " + lamp.getId() + ", Lat: " + lamp.getLatitude() + ", Lon: " + lamp.getLongitude() + ", On: " + lamp.isOn() + ", Priority: " + lamp.getPriority());
                }
                // Save the generated street lamp coordinates to Firebase
//                saveStreetLampsToFirebase(lamps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

/*    private void saveStreetLampsToFirebase(List<StreetLamp> streetLamps) {
        for (StreetLamp lamp : streetLamps) {
            String lampKey = myRef.push().getKey();
            if (lampKey != null) {
                myRef.child(lampKey).setValue(lamp);
            }
        }

    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervision, container, false);
    }


    @SuppressLint("PotentialBehaviorOverride")
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
                TextView tvMapPriority = view.findViewById(R.id.tvMapPriority);
                TextView tvMapZone = view.findViewById(R.id.tvMapZoneName);

                tvMapId.setText(marker.getTitle());
                String[] snippets = Objects.requireNonNull(marker.getSnippet()).split("\n");
                tvMapLatitude.setText(snippets[0]);
                tvMapLongitude.setText(snippets[1]);
                tvMapPriority.setText(snippets[2]);
                tvMapZone.setText(snippets[3]);


                return view;
            }
        });

        LatLng targetLocation = new LatLng(35.6234197, -5.2840685);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 14.0f));


/*        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    StreetLamp lamp = postSnapshot.getValue(StreetLamp.class);
                    assert lamp != null;
                    LatLng lampLocation = new LatLng(lamp.getLatitude(), lamp.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(lampLocation)
                            .icon(lamp.isOn() ? lampOnIcon : lampOffIcon)
                            .title(lamp.getId())
                            .snippet(lamp.getLatitude() + "\n" + lamp.getLongitude() + "\n" + lamp.getPriority() + "\n" + lamp.getZoneName()); // Fetch the priority here
                    mMap.addMarker(markerOptions);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "", error.toException());
            }
        });
        lampQuery = myRef.orderByKey().limitToFirst(50);
        loadLamps();
    }*/
    /*private void loadLamps() {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "", error.toException());
            }
        });
    }*/
    }
}