package com.example.gestion_materielle_hackathon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.gestion_materielle_hackathon.Adapters.LampAdapter;
import com.example.gestion_materielle_hackathon.model.Lamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Classification extends Fragment {


    private RecyclerView recyclerView;
    private LampAdapter lampAdapter;

    private Button bHigh, bMedium, bLow,bAll;

    private DatabaseReference lampsRef,equipesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        lampsRef = FirebaseDatabase.getInstance().getReference("lamps");
        equipesRef = FirebaseDatabase.getInstance().getReference("equipes");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch the data from the database
        List<Lamp> lamps = fetchLamps();

        bHigh = view.findViewById(R.id.bHigh);
        bMedium = view.findViewById(R.id.bMedium);
        bLow = view.findViewById(R.id.bLow);
        bAll = view.findViewById(R.id.bAll);
/*

        bHigh.setOnClickListener(v -> myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lamp> lamps1 = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Lamp lamp = child.getValue(Lamp.class);
                    if (lamp != null && !lamp.isOn() && lamp.getPriority().equals("high")) {
                        lamps1.add(lamp);
                    }
                }
                lampAdapter.setLamps(lamps1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Classification", "Failed to fetch lamps", error.toException());
            }
        }));
        bMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Lamp> lamps = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Lamp lamp = child.getValue(Lamp.class);
                            if (lamp != null && !lamp.isOn() && lamp.getPriority().equals("medium")) {
                                lamps.add(lamp);
                            }
                        }
                        lampAdapter.setLamps(lamps);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Classification", "Failed to fetch lamps", error.toException());
                    }
                });

            }
        });
        bLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Lamp> lamps = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Lamp lamp = child.getValue(Lamp.class);
                            if (lamp != null && !lamp.isOn() && lamp.getPriority().equals("low")) {
                                lamps.add(lamp);
                            }
                        }
                        lampAdapter.setLamps(lamps);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Classification", "Failed to fetch lamps", error.toException());
                    }
                });

            }
        });
*/


        bAll.setOnClickListener(v -> fetchLamps());

        // Set up the RecyclerView
        lampAdapter = new LampAdapter(lamps);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(lampAdapter);
    }

    private List<Lamp> fetchLamps() {
        final List<Lamp> lamps = new ArrayList<>();

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
                                    Lamp lamp = lampSnapshot.getValue(Lamp.class);
                                    if (lamp != null && !lamp.isOn()) {
                                        lamps.add(lamp);
                                    }
                                }
                            }
                            lampAdapter.setLamps(lamps);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Classification", "Failed to fetch lamps", databaseError.toException());
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "No assigned zone found for you", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Classification", "Failed to fetch equipes", databaseError.toException());
            }
        });

        return lamps;
    }
}