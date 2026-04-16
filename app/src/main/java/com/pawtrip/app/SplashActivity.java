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
            DatabaseHelper db   = new DatabaseHelper(this);
            Intent intent;
            if (!session.hasAccount()) {
                intent = new Intent(this, RegisterActivity.class);
            } else if (!session.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else if (db.getAllPets().isEmpty()) {
                intent = new Intent(this, OnboardingActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}