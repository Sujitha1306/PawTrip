package com.pawtrip.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

public class HomeFragment extends Fragment {

    DatabaseHelper db;
    FusedLocationProviderClient fusedClient;
    TextView tvGreeting, tvPetInfo, tvStats, tvLocationBadge;
    RecyclerView rvVenues;
    String detectedCity = "";
    List<Venue> currentVenues;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final int LOC_PERM = 102;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        db = new DatabaseHelper(requireContext());
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        tvGreeting     = v.findViewById(R.id.tvGreeting);
        tvPetInfo      = v.findViewById(R.id.tvPetInfo);
        tvStats        = v.findViewById(R.id.tvStats);
        tvLocationBadge = v.findViewById(R.id.tvLocationBadge);
        rvVenues       = v.findViewById(R.id.rvNearbyVenues);
        rvVenues.setLayoutManager(new LinearLayoutManager(requireContext()));

        String ownerName = db.getSetting("owner_name", "Traveller");
        List<Pet> pets   = db.getAllPets();

        tvGreeting.setText("Hello, " + ownerName + "! 👋");
        if (!pets.isEmpty()) {
            Pet p = pets.get(0);
            tvPetInfo.setText("🐾 Travelling with " + p.name + " · " + p.breed);
            if (p.isSenior()) tvPetInfo.append("\n⚠️ Senior pet — shorter routes");
        }

        // Stats card — tap to see all venues
        v.findViewById(R.id.cardStats).setOnClickListener(x ->
            showAllVenuesDialog(db.getAllVenues()));

        // Plan a trip card
        v.findViewById(R.id.cardPlanTrip).setOnClickListener(x -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).bottomNav
                    .setSelectedItemId(R.id.nav_trips);
            }
        });

        // Logout button
        v.findViewById(R.id.btnLogout).setOnClickListener(x -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (d, w) -> {
                    new UserSession(requireContext()).logout();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        // Detect location and load city venues
        detectLocationAndLoad();

        return v;
    }

    private void detectLocationAndLoad() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedClient.getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    reverseGeocode(loc.getLatitude(), loc.getLongitude());
                } else {
                    loadDefaultVenues();
                }
            });
        } else {
            requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code,
            @NonNull String[] perms, @NonNull int[] results) {
        if (code == LOC_PERM && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            detectLocationAndLoad();
        } else {
            loadDefaultVenues();
        }
    }

    private void reverseGeocode(double lat, double lng) {
        executor.execute(() -> {
            try {
                Geocoder gc = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addrs = gc.getFromLocation(lat, lng, 1);
                if (addrs != null && !addrs.isEmpty()) {
                    String city = addrs.get(0).getLocality();
                    if (city == null) city = addrs.get(0).getSubAdminArea();
                    final String finalCity = city != null ? city : "";
                    mainHandler.post(() -> {
                        detectedCity = finalCity;
                        if (tvLocationBadge != null) {
                            tvLocationBadge.setText("📍 " + finalCity);
                            tvLocationBadge.setVisibility(View.VISIBLE);
                        }
                        loadVenuesForCity(finalCity);
                    });
                } else {
                    mainHandler.post(this::loadDefaultVenues);
                }
            } catch (Exception e) {
                mainHandler.post(this::loadDefaultVenues);
            }
        });
    }

    private void loadVenuesForCity(String city) {
        executor.execute(() -> {
            List<Venue> cityVenues = db.getVenuesByCity(city);
            if (cityVenues.isEmpty()) cityVenues = db.getAllVenues();
            final List<Venue> venues = cityVenues;
            mainHandler.post(() -> {
                currentVenues = venues;
                tvStats.setText(venues.size() + " pet-friendly spots in "
                    + (city.isEmpty() ? "database" : city));
                rvVenues.setAdapter(new VenueAdapter(
                    venues.subList(0, Math.min(5, venues.size())),
                    requireContext(), this::showVenueDetail));
            });
        });
    }

    private void loadDefaultVenues() {
        List<Venue> all = db.getAllVenues();
        currentVenues = all;
        tvStats.setText(all.size() + " pet-friendly venues in database");
        if (tvLocationBadge != null) tvLocationBadge.setVisibility(View.GONE);
        rvVenues.setAdapter(new VenueAdapter(
            all.subList(0, Math.min(5, all.size())),
            requireContext(), this::showVenueDetail));
    }

    private void showAllVenuesDialog(List<Venue> venues) {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_all_venues, null);
        androidx.recyclerview.widget.RecyclerView rv =
            dialogView.findViewById(R.id.rvCityGroups);
        rv.setLayoutManager(
            new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        rv.setAdapter(new VenueCityAdapter(venues, requireContext(),
            this::showVenueDetail));
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("🐾 All Pet-Friendly Venues")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show();
    }

    private void showVenueDetail(Venue venue) {
        String msg = venue.getTypeEmoji() + " " + venue.name + "\n\n"
            + "📍 City: " + (venue.city != null ? venue.city : "N/A") + "\n"
            + "📋 Type: " + venue.type + "\n"
            + "🐾 Paw Score: " + venue.pawScore + " / 5\n"
            + "🕐 Hours: " + venue.openHours + "\n"
            + "📋 Rules: " + venue.petRules + "\n"
            + "📞 Phone: " + (venue.phone != null && !venue.phone.isEmpty() ? venue.phone : "Not available");
        new AlertDialog.Builder(requireContext())
            .setTitle(venue.name)
            .setMessage(msg)
            .setPositiveButton("Got it", null)
            .show();
    }
}
