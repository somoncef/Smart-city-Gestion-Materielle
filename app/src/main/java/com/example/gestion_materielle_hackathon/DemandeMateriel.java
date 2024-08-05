package com.example.gestion_materielle_hackathon;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.gestion_materielle_hackathon.model.Demande;
import com.example.gestion_materielle_hackathon.model.Material;
import com.example.gestion_materielle_hackathon.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DemandeMateriel extends AppCompatActivity {
    private LinearLayout materialsContainer;
    private int materialCount = 1;
    private List<String> materialNames = new ArrayList<>();
    private DatabaseReference databaseReference;
    private EditText etDateDebut;
    private EditText etDateFin;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_materiel);
        etDateDebut = findViewById(R.id.et_date_debut);
        etDateFin = findViewById(R.id.et_date_fin);

        // Set up date picker dialogs
        etDateDebut.setOnClickListener(v -> showDatePickerDialog(etDateDebut));
        etDateFin.setOnClickListener(v -> showDatePickerDialog(etDateFin));
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

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDemande();
            }
        });

        setupDatePickers();
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

    private void submitDemande() {
        // Validate required fields
        if (!validateRequiredFields()) {
            return; // Exit if any required field is empty
        }

        // Collect data from UI elements
        String emplacement = ((EditText) findViewById(R.id.et_emplacement)).getText().toString();
        String dateDebut = ((EditText) findViewById(R.id.et_date_debut)).getText().toString();
        String dateFin = ((EditText) findViewById(R.id.et_date_fin)).getText().toString();
        String description = ((EditText) findViewById(R.id.et_description)).getText().toString();

        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < materialsContainer.getChildCount(); i++) {
            View child = materialsContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) child;
                AutoCompleteTextView materialNameField = (AutoCompleteTextView) linearLayout.getChildAt(0);
                EditText materialQuantityField = (EditText) linearLayout.getChildAt(1);
                EditText materialDescriptionField = (EditText) linearLayout.getChildAt(2);

                String materialName = materialNameField.getText().toString();
                String materialQuantity = materialQuantityField.getText().toString();
                String materialDescription = materialDescriptionField.getText().toString();

                if (!materialName.isEmpty() && !materialQuantity.isEmpty()) {
                    materials.add(new Material(materialName, materialQuantity, materialDescription));
                }
            }
        }

        String justification = ((EditText) findViewById(R.id.et_justification)).getText().toString();
        int selectedUrgenceId = ((RadioGroup) findViewById(R.id.rg_urgence)).getCheckedRadioButtonId();
        String urgence = ((RadioButton) findViewById(selectedUrgenceId)).getText().toString();

        // Create a Demande object
        Demande demande = new Demande(emplacement, dateDebut, dateFin, description, materials, justification, urgence);

        // Save the demande to Firebase
        fetchCurrentUserAndSaveDemande(demande);
    }

    private boolean validateRequiredFields() {
        boolean isValid = true;

        // Validate EditTexts
        EditText etEmplacement = findViewById(R.id.et_emplacement);
        EditText etDateDebut = findViewById(R.id.et_date_debut);
        EditText etDateFin = findViewById(R.id.et_date_fin);
        AutoCompleteTextView etMaterialName1 = findViewById(R.id.et_material_name_1);
        EditText etMaterialQuantity1 = findViewById(R.id.et_material_quantity_1);
        RadioGroup rgUrgence = findViewById(R.id.rg_urgence);

        // Check if any required EditText is empty
        if (etEmplacement.getText().toString().isEmpty()) {
            etEmplacement.setError("Ce champ est requis");
            isValid = false;
        }
        if (etDateDebut.getText().toString().isEmpty()) {
            etDateDebut.setError("Ce champ est requis");
            isValid = false;
        }
        if (etDateFin.getText().toString().isEmpty()) {
            etDateFin.setError("Ce champ est requis");
            isValid = false;
        }
        if (etMaterialName1.getText().toString().isEmpty()) {
            etMaterialName1.setError("Ce champ est requis");
            isValid = false;
        }
        if (etMaterialQuantity1.getText().toString().isEmpty()) {
            etMaterialQuantity1.setError("Ce champ est requis");
            isValid = false;
        }
        if (rgUrgence.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Veuillez sélectionner un niveau d'urgence", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void fetchCurrentUserAndSaveDemande(Demande demande) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");

        usersReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null) {
                    demande.setChef(currentUser);
                    saveDemandeToFirebase(demande);
                } else {
                    Toast.makeText(DemandeMateriel.this, "Failed to fetch current user's information.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DemandeMateriel.this, "Failed to fetch current user's information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDemandeToFirebase(Demande demande) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("demands");
        String demandeId = databaseReference.push().getKey(); // Generate a unique key for the new demande

        if (demandeId != null) {
            demande.setId(demandeId); // Set the id of the demande
            databaseReference.child(demandeId).setValue(demande)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DemandeMateriel.this, "Demande saved successfully.", Toast.LENGTH_SHORT).show();
                        // Optionally, clear or reset UI elements here
                        startActivity(new Intent(this, MainActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DemandeMateriel.this, "Failed to save demande.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setupDatePickers() {

        etDateDebut.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etDateDebut.getRight() - etDateDebut.getCompoundDrawables()[2].getBounds().width())) {
                    showDatePickerDialog(etDateDebut);
                    return true;
                }
            }
            return false;
        });

        etDateFin.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etDateFin.getRight() - etDateFin.getCompoundDrawables()[2].getBounds().width())) {
                    showDatePickerDialog(etDateFin);
                    return true;
                }
            }
            return false;
        });

        etDateDebut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDates();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDateFin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDates();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showDatePickerDialog(final EditText editText) {
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // Set selected date to the EditText
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(editText);
        };

        new DatePickerDialog(DemandeMateriel.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(EditText editText) {
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void validateDates() {
        String startDateString = etDateDebut.getText().toString();
        String endDateString = etDateFin.getText().toString();

        if (startDateString.isEmpty() || endDateString.isEmpty()) {
            return; // Exit if any date is not set
        }

        try {
            Date startDate = sdf.parse(startDateString);
            Date endDate = sdf.parse(endDateString);
            Date currentDate = new Date(); // Get the current date

            if (startDate != null && endDate != null) {
                if (startDate.after(endDate)) {
                    etDateFin.setError("La date de fin doit être après la date de début");
                } else if (endDate.before(currentDate)) {
                    etDateFin.setError("La date de fin ne peut pas être dans le passé");
                } else {
                    etDateFin.setError(null); // Clear error if dates are valid
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
