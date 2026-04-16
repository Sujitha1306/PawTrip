package com.pawtrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirm;
    Button btnRegister, btnGoLogin;
    UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        session = new UserSession(this);

        etName     = findViewById(R.id.etRegName);
        etEmail    = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirm  = findViewById(R.id.etRegConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin  = findViewById(R.id.btnGoLogin);

        btnRegister.setOnClickListener(v -> {
            String name    = etName.getText().toString().trim();
            String email   = etEmail.getText().toString().trim();
            String pass    = etPassword.getText().toString().trim();
            String confirm = etConfirm.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
                return;
            }

            // Save account
            session.saveUser(name, email, pass);

            // Save to DB settings too
            DatabaseHelper db = new DatabaseHelper(this);
            db.saveSetting("owner_name", name);
            db.saveSetting("owner_email", email);

            // Go to pet setup next
            Toast.makeText(this, "Account created! Now set up your pet.",
                Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        });

        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}