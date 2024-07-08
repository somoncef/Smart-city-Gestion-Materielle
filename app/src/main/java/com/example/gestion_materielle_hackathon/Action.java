package com.example.gestion_materielle_hackathon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gestion_materielle_hackathon.model.LampMarkerOptions;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;


public class Action extends Fragment  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FloatingActionButton fabChat;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabChat = view.findViewById(R.id.fabChat);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }

        fabChat.setOnClickListener(v -> {
            // Add code to open chat fragment
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng targetLocation = new LatLng(35.5851307, -5.4077635);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15));

        BitmapDescriptor lampOnIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampon);
        BitmapDescriptor lampOffIcon = BitmapDescriptorFactory.fromResource(R.drawable.lampoff);

        double range = 0.01;

        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            double randomLat = targetLocation.latitude + range * (random.nextDouble() * 2 - 1);
            double randomLng = targetLocation.longitude + range * (random.nextDouble() * 2 - 1);
            LatLng randomLocation = new LatLng(randomLat, randomLng);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(randomLocation)
                    .icon(lampOffIcon);
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag("lamp" + i);
        }
        mMap.setOnMarkerClickListener(marker -> {
            String lampId = (String) marker.getTag();
            TextView lampadaireIdTextView = getActivity().findViewById(R.id.lampadaire_id);
            lampadaireIdTextView.setText("Lampadaire - " + lampId + ": ");
            return false;
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action, container, false);
    }
}



