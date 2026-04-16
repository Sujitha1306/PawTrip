package com.pawtrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnGoRegister;
    UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new UserSession(this);

        etEmail       = findViewById(R.id.etLoginEmail);
        etPassword    = findViewById(R.id.etLoginPassword);
        btnLogin      = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.equalsIgnoreCase(session.getEmail())) {
                Toast.makeText(this, "No account found with this email",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (!session.checkPassword(pass)) {
                Toast.makeText(this, "Incorrect password",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            // Mark as logged in
            session.setLoggedIn(true);

            // Check if pet profile exists
            DatabaseHelper db = new DatabaseHelper(this);
            if (db.getAllPets().isEmpty()) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        });

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}