package com.pawtrip.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

public class SosFragment extends Fragment {

    private static final int LOCATION_PERMISSION_CODE = 101;
    DatabaseHelper db;
    FusedLocationProviderClient fusedClient;
    TextView tvNearestVet, tvVetPhone, tvStatus, tvCurrentLocation;
    Button btnSOS, btnCallVet, btnSendEmail, btnChangeLocation;
    Venue nearestVet = null;
    double currentLat = 0, currentLng = 0;
    boolean locationSet = false;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_sos, container, false);
        db = new DatabaseHelper(requireContext());
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        tvNearestVet      = v.findViewById(R.id.tvNearestVet);
        tvVetPhone        = v.findViewById(R.id.tvVetPhone);
        tvStatus          = v.findViewById(R.id.tvStatus);
        tvCurrentLocation = v.findViewById(R.id.tvCurrentLocation);
        btnSOS            = v.findViewById(R.id.btnSOS);
        btnCallVet        = v.findViewById(R.id.btnCallVet);
        btnSendEmail      = v.findViewById(R.id.btnSendEmail);
        btnChangeLocation = v.findViewById(R.id.btnChangeLocation);

        btnSOS.setOnClickListener(x -> triggerFullSOS());
        btnCallVet.setOnClickListener(x -> callVet());
        btnSendEmail.setOnClickListener(x -> sendSosEmail());
        btnChangeLocation.setOnClickListener(x -> showEnterAddressDialog());

        requestLocationOrAsk();
        return v;
    }

    private void requestLocationOrAsk() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getGPSLocation();
        } else {
            // Ask for permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms,
            @NonNull int[] results) {
        if (code == LOCATION_PERMISSION_CODE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSLocation();
            } else {
                // Permission denied — ask to enter address manually
                tvCurrentLocation.setText("📍 Location: Not set — enter address below");
                showEnterAddressDialog();
            }
        }
    }

    private void getGPSLocation() {
        tvCurrentLocation.setText("📍 Getting your location...");
        try {
            fusedClient.getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    currentLat = loc.getLatitude();
                    currentLng = loc.getLongitude();
                    locationSet = true;
                    // Reverse geocode to show city name
                    try {
                        Geocoder gc = new Geocoder(requireContext(), Locale.getDefault());
                        List<Address> addrs = gc.getFromLocation(currentLat, currentLng, 1);
                        if (addrs != null && !addrs.isEmpty()) {
                            String city = addrs.get(0).getLocality();
                            tvCurrentLocation.setText("📍 Your location: " + city);
                        } else {
                            tvCurrentLocation.setText("📍 GPS location obtained");
                        }
                    } catch (Exception e) {
                        tvCurrentLocation.setText("📍 GPS location obtained");
                    }
                    findNearestVet();
                } else {
                    tvCurrentLocation.setText("📍 GPS unavailable — enter address");
                    showEnterAddressDialog();
                }
            });
        } catch (SecurityException e) {
            showEnterAddressDialog();
        }
    }

    private void showEnterAddressDialog() {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_enter_address, null);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);
        new AlertDialog.Builder(requireContext())
            .setTitle("📍 Enter Your Location")
            .setMessage("Enter your city or area to find the nearest vet:")
            .setView(dialogView)
            .setPositiveButton("Search", (d, w) -> {
                String address = etAddress.getText().toString().trim();
                if (!address.isEmpty()) geocodeAddress(address);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void geocodeAddress(String addressText) {
        tvCurrentLocation.setText("📍 Searching: " + addressText + "...");
        executor.execute(() -> {
            try {
                Geocoder gc = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> results = gc.getFromLocationName(addressText, 1);
                if (results != null && !results.isEmpty()) {
                    currentLat = results.get(0).getLatitude();
                    currentLng = results.get(0).getLongitude();
                    locationSet = true;
                    String city = results.get(0).getLocality() != null
                        ? results.get(0).getLocality()
                        : addressText;
                    mainHandler.post(() -> {
                        tvCurrentLocation.setText("📍 Location: " + city);
                        findNearestVet();
                    });
                } else {
                    mainHandler.post(() -> {
                        tvCurrentLocation.setText("📍 Address not found. Try again.");
                        Toast.makeText(requireContext(),
                            "Could not find that address", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() ->
                    Toast.makeText(requireContext(),
                        "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void findNearestVet() {
        tvNearestVet.setText("🔍 Finding nearest vet...");
        executor.execute(() -> {
            List<Venue> vets = db.getVenuesByType("vet");
            double minDist = Double.MAX_VALUE;
            Venue closest = null;
            for (Venue vet : vets) {
                double dist = haversine(currentLat, currentLng, vet.latitude, vet.longitude);
                if (dist < minDist) { minDist = dist; closest = vet; }
            }
            final Venue found = closest;
            final double dist = minDist;
            mainHandler.post(() -> {
                if (isAdded()) {
                    nearestVet = found;
                    if (found != null) {
                        tvNearestVet.setText("🏥 " + found.name
                            + "\n📏 " + String.format("%.1f", dist) + " km away");
                        tvVetPhone.setText("📞 " + (found.phone.isEmpty()
                            ? "No number saved" : found.phone));
                        tvStatus.setText("🕐 Hours: " + found.openHours);
                    } else {
                        tvNearestVet.setText("No vet found in database");
                    }
                }
            });
        });
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
            + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    private void triggerFullSOS() {
        if (!locationSet) {
            Toast.makeText(requireContext(), "Please set your location first", Toast.LENGTH_SHORT).show();
            showEnterAddressDialog();
            return;
        }
        tvStatus.setText("🚨 SOS triggered! Calling vet + sending email...");
        callVet();
        sendSosEmail();
    }

    private void callVet() {
        if (nearestVet != null && !nearestVet.phone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + nearestVet.phone));
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "No vet phone number available", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSosEmail() {
        String ownerEmail = db.getSetting("owner_email", "");
        if (ownerEmail.isEmpty()) {
            Toast.makeText(requireContext(), "No email in profile", Toast.LENGTH_SHORT).show();
            return;
        }
        String vetInfo = nearestVet != null
            ? nearestVet.name + " | " + nearestVet.phone : "Unknown";
        String body = "PET EMERGENCY ALERT\n\n"
            + "GPS: " + currentLat + ", " + currentLng + "\n"
            + "Map: https://maps.google.com/?q=" + currentLat + "," + currentLng + "\n\n"
            + "Nearest Vet: " + vetInfo + "\n\n"
            + "Sent from PawTrip Emergency Mode";
        EmailSender.sendEmail(ownerEmail, "🚨 PET EMERGENCY", body, requireContext());
    }
}
