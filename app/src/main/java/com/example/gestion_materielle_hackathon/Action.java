package com.example.gestion_materielle_hackathon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


public class Action extends Fragment  {

CardView cardMateriel;
    DatabaseReference databaseReference;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        cardMateriel = view.findViewById(R.id.cardMateriel);

        // Initially hide the cardMateriel
        cardMateriel.setVisibility(View.GONE);

        // Fetch the assignedZone from Firebase
        DatabaseReference equipesReference = FirebaseDatabase.getInstance().getReference("equipes");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        equipesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot equipeSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot chefSnapshot = equipeSnapshot.child("chef");
                    if (chefSnapshot.exists()) {
                        String chefId = chefSnapshot.child("id").getValue(String.class);
                        if (currentUserId.equals(chefId)) {
                            String assignedZone = chefSnapshot.child("assignedZone").getValue(String.class);
                            if (assignedZone != null) {
                                // If assignedZone is not null, display the cardMateriel
                                cardMateriel.setVisibility(View.VISIBLE);
                                break; // Exit the loop as soon as we find a valid assignedZone
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to fetch assignedZone.", Toast.LENGTH_SHORT).show();
            }
        });

        cardMateriel.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DemandeMateriel.class));
        });

        fabChat.setOnClickListener(v -> {
            // Add code to open chat fragment
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_action, container, false);
    }

}



