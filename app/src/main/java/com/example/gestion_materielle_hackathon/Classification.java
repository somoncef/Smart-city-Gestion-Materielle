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

import com.example.gestion_materielle_hackathon.Adapters.LampAdapter;
import com.example.gestion_materielle_hackathon.model.Lamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Classification extends Fragment {


    private RecyclerView recyclerView;
    private LampAdapter lampAdapter;

    private DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classification, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        myRef = FirebaseDatabase.getInstance().getReference("lamps");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch the data from the database
        List<Lamp> lamps = fetchLamps();

        // Set up the RecyclerView
        lampAdapter = new LampAdapter(lamps);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(lampAdapter);
    }

    private List<Lamp> fetchLamps() {
        // Fetch the lamps with on = false from the database and return them
        // This is a placeholder and needs to be replaced with actual code

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lamp> lamps = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Lamp lamp = child.getValue(Lamp.class);
                    if (lamp != null && !lamp.isOn()) {
                        lamps.add(lamp);
                    }
                }
                lampAdapter.setLamps(lamps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Classification", "Failed to fetch lamps", error.toException());
            }
        }
        );

        return new ArrayList<>();
    }
}