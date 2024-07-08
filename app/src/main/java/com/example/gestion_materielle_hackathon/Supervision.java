package com.example.gestion_materielle_hackathon;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.gestion_materielle_hackathon.model.Lamp;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;


public class Supervision extends Fragment  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference myRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the permission is not granted, request the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("lamps");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervision, container, false);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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

    }

/*    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return null;  // Use the default InfoWindow frame
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);

                TextView tvMapUser = view.findViewById(R.id.tvMapUser);
                TextView tvMapDifficulty = view.findViewById(R.id.tvMapDifficulty);
                TextView tvMapScore = view.findViewById(R.id.tvMapScore);

                tvMapUser.setText(marker.getTitle());
                String[] snippets = Objects.requireNonNull(marker.getSnippet()).split("\n");
                tvMapDifficulty.setText(snippets[0]);
                tvMapScore.setText(snippets[1]);

                return view;
            }
        });
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10.0f));
        firestore.collection("Quizz").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String userId = document.getString("userId");
                    Long score = document.getLong("score");
                    String difficultyLevel = document.getString("difficultyLevel");

                    firestore.collection("User").document(Objects.requireNonNull(userId)).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document1 = task1.getResult();
                            String id = document1.getId();
                            String userName = document1.getString("name");
                            Double userLat = document1.getDouble("userLat");
                            Double userLng = document1.getDouble("userLng");


                            if (userLat != null && userLng != null) {
                                LatLng userLocation = new LatLng(userLat, userLng);
                                MarkerOptions markerOptions = new MarkerOptions().position(userLocation).title(userName).snippet(difficultyLevel + "\n" + score);

                                switch (Objects.requireNonNull(difficultyLevel)) {
                                    case "Hard":
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        break;
                                    case "Medium":
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        break;
                                    case "Easy":
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        break;
                                }

                                if (userId.equals(UserID)) {
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                }

                                map.addMarker(markerOptions);
                            } else {
                                Log.d(MotionEffect.TAG, "Error getting documents: ", task1.getException());
                            }
                        }
                    });
                }
            } else {
                Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
            }
        });
    }*/
}