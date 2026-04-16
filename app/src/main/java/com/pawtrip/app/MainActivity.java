package com.pawtrip.app;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNav;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar   = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);
        setSupportActionBar(toolbar);

        loadFragment(new HomeFragment(), "Home");

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment f;
            String title;
            int id = item.getItemId();
            if      (id == R.id.nav_home)  { f = new HomeFragment();  title = "PawTrip 🐾"; }
            else if (id == R.id.nav_map)   { f = new MapFragment();   title = "Pet Map 🗺️"; }
            else if (id == R.id.nav_trips) { f = new TripsFragment(); title = "My Trips 🧳"; }
            else if (id == R.id.nav_tips)  { f = new TipsFragment();  title = "Travel Tips 📰"; }
            else if (id == R.id.nav_sos)   { f = new SosFragment();   title = "Emergency 🚨"; }
            else return false;
            loadFragment(f, title);
            return true;
        });
    }

    public void loadFragment(Fragment f, String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, f)
            .commit();
    }
}