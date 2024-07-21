package com.example.gestion_materielle_hackathon;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AssignementConfirmation extends AppCompatActivity {

    TextView tvZone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignement_confirmation);

        tvZone = findViewById(R.id.tvZone);

        boolean assignedZone = getIntent().getBooleanExtra("assigned_zone", false);
        String zoneName = getIntent().getStringExtra("zone_name");
        tvZone.setText("Vous avez été affecté à la zone " + zoneName);
    }
}