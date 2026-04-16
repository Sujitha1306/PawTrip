package com.pawtrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    EditText etOwnerName, etPetName, etBreed, etAge, etWeight, etEmail, etHealthNotes;
    Button btnSave;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        db = new DatabaseHelper(this);

        etOwnerName   = findViewById(R.id.etOwnerName);
        etPetName     = findViewById(R.id.etPetName);
        etBreed       = findViewById(R.id.etBreed);
        etAge         = findViewById(R.id.etAge);
        etWeight      = findViewById(R.id.etWeight);
        etEmail       = findViewById(R.id.etEmail);
        etHealthNotes = findViewById(R.id.etHealthNotes);
        btnSave       = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String petName = etPetName.getText().toString().trim();
        String breed   = etBreed.getText().toString().trim();
        String ageStr  = etAge.getText().toString().trim();
        String wtStr   = etWeight.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String owner   = etOwnerName.getText().toString().trim();
        String notes   = etHealthNotes.getText().toString().trim();

        if (petName.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in pet name, age and your email", Toast.LENGTH_SHORT).show();
            return;
        }

        Pet pet = new Pet();
        pet.name = petName;
        pet.breed = breed.isEmpty() ? "Mixed" : breed;
        pet.age = Integer.parseInt(ageStr);
        pet.weight = wtStr.isEmpty() ? 0 : Double.parseDouble(wtStr);
        pet.ownerEmail = email;
        pet.healthNotes = notes;
        db.insertPet(pet);
        db.saveSetting("owner_name", owner);
        db.saveSetting("owner_email", email);

        UserSession session = new UserSession(this);
        if (!session.isLoggedIn()) {
            // Save session as logged in after onboarding if not already
            session.saveUser(owner, email, "pawtrip");
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}