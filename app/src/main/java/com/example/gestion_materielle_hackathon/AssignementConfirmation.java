package com.example.gestion_materielle_hackathon;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssignementConfirmation extends AppCompatActivity {

    TextView tvZone;
    Button bAccept, bReject;
    DatabaseReference equipesRef;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignement_confirmation);
        equipesRef = FirebaseDatabase.getInstance().getReference("equipes");

        tvZone = findViewById(R.id.tvZone);
        bAccept = findViewById(R.id.bAccept);
        bReject = findViewById(R.id.bReject);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        String zoneName = getIntent().getStringExtra("zone_name");
        tvZone.setText("Vous avez été affecté à " + zoneName);

        bAccept.setOnClickListener(v -> {
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            updateChefNode("assignedZone", zoneName);
            updateChefNode("startDateTime", currentDateTime);
        });


        bReject.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Rejection")
                    .setMessage("Are you sure you want to reject the assignment?")
                    .setPositiveButton("Yes", (dialog, which) -> updateChefNode("assignedZone", null))
                    .setNegativeButton("No", null)
                    .show();
        });
    }
    private void updateChefNode(String childName, String value) {
        equipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot equipeSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot chefSnapshot = equipeSnapshot.child("chef");
                    String chefId = chefSnapshot.child("id").getValue(String.class);
                    if (currentUserId.equals(chefId)) {
                        if (value == null) {
                            chefSnapshot.getRef().child(childName).removeValue();
                            chefSnapshot.getRef().child("startDateTime").removeValue();
                            startActivity(new Intent(AssignementConfirmation.this, MainActivity.class));
                            finish();
                        } else {
                            chefSnapshot.getRef().child(childName).setValue(value);
                            startActivity(new Intent(AssignementConfirmation.this, MainActivity.class));
                            finish();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
