package com.pawtrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    EditText etPetName, etBreed, etAge, etWeight, etHealthNotes;
    Button btnSave;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        db = new DatabaseHelper(this);

        etPetName    = findViewById(R.id.etPetName);
        etBreed      = findViewById(R.id.etBreed);
        etAge        = findViewById(R.id.etAge);
        etWeight     = findViewById(R.id.etWeight);
        etHealthNotes = findViewById(R.id.etHealthNotes);
        btnSave      = findViewById(R.id.btnSave);

        // Pre-fill owner name from session
        UserSession session = new UserSession(this);
        TextView tvWelcome = findViewById(R.id.tvWelcomeMsg);
        tvWelcome.setText("Welcome, " + session.getName()
            + "!\nTell us about your pet 🐾");

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String petName = etPetName.getText().toString().trim();
        String breed   = etBreed.getText().toString().trim();
        String ageStr  = etAge.getText().toString().trim();
        String wtStr   = etWeight.getText().toString().trim();
        String notes   = etHealthNotes.getText().toString().trim();

        if (petName.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Pet name and age are required",
                Toast.LENGTH_SHORT).show();
            return;
        }

        Pet pet = new Pet();
        pet.name = petName;
        pet.breed = breed.isEmpty() ? "Mixed" : breed;
        pet.age   = Integer.parseInt(ageStr);
        pet.weight = wtStr.isEmpty() ? 0 : Double.parseDouble(wtStr);
        pet.healthNotes = notes;

        UserSession session = new UserSession(this);
        pet.ownerEmail = session.getEmail();

        db.insertPet(pet);

        Toast.makeText(this, petName + " added! Let's go! 🐾",
            Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}