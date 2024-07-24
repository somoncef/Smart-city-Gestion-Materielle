package com.example.gestion_materielle_hackathon;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DemandeMateriel extends AppCompatActivity {
    private LinearLayout materialsContainer;
    private int materialCount = 1;
    private List<String> materialNames = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_materiel);

        materialsContainer = findViewById(R.id.materials_container);
        Button addMaterialButton = findViewById(R.id.btn_add_material);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("tools");

        // Fetch material names from Firebase
        fetchMaterialNamesFromFirebase();

        addMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMaterialField();
            }
        });
    }

    private void fetchMaterialNamesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                materialNames.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot materialSnapshot : categorySnapshot.getChildren()) {
                        String materialName = materialSnapshot.child("name").getValue(String.class);
                        if (materialName != null) {
                            materialNames.add(materialName);
                            Log.d("MaterialName", "Material Name: " + materialName); // Debug log
                        }
                    }
                }
                if (materialNames.isEmpty()) {
                    Toast.makeText(DemandeMateriel.this, "No materials found.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MaterialList", "Materials: " + materialNames.toString()); // Debug log

                    // Set the adapter for existing AutoCompleteTextView
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DemandeMateriel.this,
                            android.R.layout.simple_dropdown_item_1line, materialNames);

                    // Set adapter for the initial AutoCompleteTextView
                    AutoCompleteTextView initialMaterialName = findViewById(R.id.et_material_name_1);
                    initialMaterialName.setAdapter(adapter);
                    initialMaterialName.setThreshold(1); // Start filtering after 1 character

                    // Update adapter for dynamically added fields
                    for (int i = 0; i < materialsContainer.getChildCount(); i++) {
                        View child = materialsContainer.getChildAt(i);
                        if (child instanceof LinearLayout) {
                            LinearLayout linearLayout = (LinearLayout) child;
                            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                                View field = linearLayout.getChildAt(j);
                                if (field instanceof AutoCompleteTextView) {
                                    AutoCompleteTextView materialNameField = (AutoCompleteTextView) field;
                                    materialNameField.setAdapter(adapter);
                                    materialNameField.setThreshold(1); // Start filtering after 1 character
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DemandeMateriel.this, "Failed to load materials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMaterialField() {
        materialCount++;

        LinearLayout newMaterialLayout = new LinearLayout(this);
        newMaterialLayout.setOrientation(LinearLayout.HORIZONTAL);
        newMaterialLayout.setPadding(0, 0, 0, 16);

        AutoCompleteTextView materialName = new AutoCompleteTextView(this);
        materialName.setHint("Nom du Matériel");
        materialName.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
        materialName.setHintTextColor(getResources().getColor(android.R.color.white));
        materialName.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));

        // Set adapter with material names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, materialNames);
        materialName.setAdapter(adapter);

        materialName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        EditText materialQuantity = new EditText(this);
        materialQuantity.setHint("Quantité");
        materialQuantity.setTextColor(getResources().getColor(R.color.grey_text));
        materialQuantity.setHintTextColor(getResources().getColor(android.R.color.white));
        materialQuantity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        materialQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        materialQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        EditText materialDescription = new EditText(this);
        materialDescription.setHint("Commentaires");
        materialDescription.setTextColor(getResources().getColor(R.color.grey_text));
        materialDescription.setHintTextColor(getResources().getColor(android.R.color.white));
        materialDescription.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        materialDescription.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));

        newMaterialLayout.addView(materialName);
        newMaterialLayout.addView(materialQuantity);
        newMaterialLayout.addView(materialDescription);

        materialsContainer.addView(newMaterialLayout);
    }
}
