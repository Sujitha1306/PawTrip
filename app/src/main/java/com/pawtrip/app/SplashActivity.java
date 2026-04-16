package com.pawtrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            UserSession session = new UserSession(this);
            Intent intent;
            if (!session.hasAccount()) {
                // No account at all - go to Register
                intent = new Intent(this, RegisterActivity.class);
            } else if (!session.isLoggedIn()) {
                // Has account but not logged in - go to Login
                intent = new Intent(this, LoginActivity.class);
            } else {
                // Logged in - check if pet is set up
                DatabaseHelper db = new DatabaseHelper(this);
                if (db.getAllPets().isEmpty()) {
                    intent = new Intent(this, OnboardingActivity.class);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}