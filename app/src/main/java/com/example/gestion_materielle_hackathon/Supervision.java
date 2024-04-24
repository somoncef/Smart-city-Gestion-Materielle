package com.example.gestion_materielle_hackathon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.gestion_materielle_hackathon.model.LampMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;


public class Supervision extends Fragment  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervision, container, false);


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
        for (int i = 0; i < 15; i++) {
            double randomLat = targetLocation.latitude + range * (random.nextDouble() * 2 - 1);
            double randomLng = targetLocation.longitude + range * (random.nextDouble() * 2 - 1);
            LatLng randomLocation = new LatLng(randomLat, randomLng);
            LampMarkerOptions markerOptions = new LampMarkerOptions()
                    .position(randomLocation)
                    .icon(i % 2 == 0 ? lampOnIcon : lampOffIcon)
                    .id("lamp" + i)
                    .lampType(i % 2 == 0 ? "on" : "off");
            mMap.addMarker(markerOptions);
        }
    }

}