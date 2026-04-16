package com.pawtrip.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    EditText etPetName, etBreed, etAge, etWeight, etHealthNotes;
    Button btnSave, btnUploadPhoto, btnUploadProof;
    ImageView ivPetPhoto;
    TextView tvPhotoStatus, tvProofStatus;
    DatabaseHelper db;

    // Stores selected URIs (optional — not blocking save)
    private Uri selectedPhotoUri = null;
    private Uri selectedProofUri = null;

    // ── Gallery picker for pet photo ──────────────────────────────────────────
    private final ActivityResultLauncher<Intent> photoPickerLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedPhotoUri = result.getData().getData();
                    ivPetPhoto.setImageURI(selectedPhotoUri);
                    String name = getFileName(selectedPhotoUri);
                    tvPhotoStatus.setText("✅ " + (name != null ? name : "Photo selected"));
                }
            });

    // ── Document picker for vaccination / ownership proof ─────────────────────
    private final ActivityResultLauncher<Intent> proofPickerLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedProofUri = result.getData().getData();
                    String name = getFileName(selectedProofUri);
                    tvProofStatus.setText("✅ " + (name != null ? name : "Document selected"));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        db = new DatabaseHelper(this);

        // ── Existing fields (unchanged) ───────────────────────────────────────
        etPetName     = findViewById(R.id.etPetName);
        etBreed       = findViewById(R.id.etBreed);
        etAge         = findViewById(R.id.etAge);
        etWeight      = findViewById(R.id.etWeight);
        etHealthNotes = findViewById(R.id.etHealthNotes);
        btnSave       = findViewById(R.id.btnSave);

        UserSession session = new UserSession(this);
        TextView tvWelcome = findViewById(R.id.tvWelcomeMsg);
        tvWelcome.setText("Welcome, " + session.getName()
            + "!\nTell us about your pet 🐾");

        btnSave.setOnClickListener(v -> saveProfile());

        // ── New upload widgets ────────────────────────────────────────────────
        ivPetPhoto    = findViewById(R.id.ivPetPhoto);
        tvPhotoStatus = findViewById(R.id.tvPhotoStatus);
        tvProofStatus = findViewById(R.id.tvProofStatus);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        btnUploadProof = findViewById(R.id.btnUploadProof);

        btnUploadPhoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pick.setType("image/*");
            photoPickerLauncher.launch(pick);
        });

        btnUploadProof.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
            pick.setType("*/*");
            // Allow PDFs and images
            pick.putExtra(Intent.EXTRA_MIME_TYPES,
                new String[]{"application/pdf", "image/*"});
            proofPickerLauncher.launch(Intent.createChooser(pick,
                "Select Vaccination / Ownership Proof"));
        });
    }

    // ── Existing saveProfile() — completely unchanged ─────────────────────────
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

        // Optionally persist photo/proof URIs in SharedPreferences
        if (selectedPhotoUri != null) {
            session.saveExtra("pet_photo_uri", selectedPhotoUri.toString());
        }
        if (selectedProofUri != null) {
            session.saveExtra("pet_proof_uri", selectedProofUri.toString());
        }

        db.insertPet(pet);

        Toast.makeText(this, petName + " added! Let's go! 🐾",
            Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /** Returns a human-readable file name from a Uri, or null if unavailable. */
    private String getFileName(Uri uri) {
        try {
            String path = uri.getLastPathSegment();
            if (path != null && path.contains("/")) {
                path = path.substring(path.lastIndexOf("/") + 1);
            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }
}