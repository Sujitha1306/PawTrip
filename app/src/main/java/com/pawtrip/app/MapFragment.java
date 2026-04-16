package com.pawtrip.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.*;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

public class MapFragment extends Fragment {

    MapView osmMap;
    FusedLocationProviderClient fusedClient;
    DatabaseHelper db;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Handler mainHandler = new Handler(Looper.getMainLooper());
    String currentFilter = "all";
    String detectedCity = "";
    MyLocationNewOverlay locationOverlay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle s) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().load(requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        db = new DatabaseHelper(requireContext());
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        osmMap = v.findViewById(R.id.osmMap);
        setupMap();

        v.findViewById(R.id.btnAll).setOnClickListener(x -> { currentFilter = "all"; refreshMarkers(); });
        v.findViewById(R.id.btnParks).setOnClickListener(x -> { currentFilter = "park"; refreshMarkers(); });
        v.findViewById(R.id.btnCafes).setOnClickListener(x -> { currentFilter = "cafe"; refreshMarkers(); });
        v.findViewById(R.id.btnVets).setOnClickListener(x -> { currentFilter = "vet"; refreshMarkers(); });
        v.findViewById(R.id.btnHotels).setOnClickListener(x -> { currentFilter = "hotel"; refreshMarkers(); });

        return v;
    }

    private void setupMap() {
        osmMap.setTileSource(TileSourceFactory.MAPNIK);
        osmMap.setMultiTouchControls(true);
        // Fix: Use non-deprecated zoom controller visibility
        osmMap.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        osmMap.getController().setZoom(14.0);

        locationOverlay = new MyLocationNewOverlay(
            new GpsMyLocationProvider(requireContext()), osmMap);
        locationOverlay.enableMyLocation();
        osmMap.getOverlays().add(locationOverlay);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedClient.getLastLocation().addOnSuccessListener(loc -> {
                GeoPoint center = loc != null
                    ? new GeoPoint(loc.getLatitude(), loc.getLongitude())
                    : new GeoPoint(13.0827, 80.2707);
                osmMap.getController().setCenter(center);
                osmMap.getController().animateTo(center);
                if (loc != null) detectCityAndFilter(loc.getLatitude(), loc.getLongitude());
            });
        } else {
            osmMap.getController().setCenter(new GeoPoint(13.0827, 80.2707));
        }

        refreshMarkers();
    }

    private void detectCityAndFilter(double lat, double lng) {
        executor.execute(() -> {
            try {
                Geocoder gc = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String city = addresses.get(0).getLocality();
                    if (city != null) {
                        detectedCity = city;
                        mainHandler.post(this::refreshMarkers);
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    private void refreshMarkers() {
        executor.execute(() -> {
            List<Venue> venuesList;
            if (!detectedCity.isEmpty() && currentFilter.equals("all")) {
                venuesList = db.getVenuesByCity(detectedCity);
                if (venuesList.isEmpty()) {
                    venuesList = db.getAllVenues();
                }
            } else if (currentFilter.equals("all")) {
                venuesList = db.getAllVenues();
            } else {
                venuesList = db.getVenuesByType(currentFilter);
            }

            final List<Venue> venues = venuesList;
            mainHandler.post(() -> {
                if (!isAdded()) return;
                osmMap.getOverlays().clear();
                osmMap.getOverlays().add(locationOverlay);

                for (Venue venue : venues) {
                    final Venue finalVenue = venue;
                    GeoPoint point = new GeoPoint(finalVenue.latitude, finalVenue.longitude);
                    Marker marker = new Marker(osmMap) {
                        @Override
                        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
                            if (shadow) return;
                            Point screenPoint = mapView.getProjection().toPixels(getPosition(), null);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            switch (finalVenue.type) {
                                case "vet":   paint.setColor(Color.parseColor("#D85A30")); break;
                                case "cafe":  paint.setColor(Color.parseColor("#EF9F27")); break;
                                case "hotel": paint.setColor(Color.parseColor("#185FA5")); break;
                                default:      paint.setColor(Color.parseColor("#1D9E75")); break;
                            }
                            canvas.drawCircle(screenPoint.x, screenPoint.y, 28, paint);
                            paint.setColor(Color.WHITE);
                            paint.setTextSize(28);
                            paint.setTextAlign(Paint.Align.CENTER);
                            canvas.drawText(finalVenue.getTypeEmoji(), screenPoint.x, screenPoint.y + 10, paint);
                        }
                    };
                    marker.setPosition(point);
                    marker.setTitle(finalVenue.name);
                    marker.setSnippet("🐾 " + finalVenue.pawScore + " | " + finalVenue.openHours + "\n" + finalVenue.petRules);
                    marker.setOnMarkerClickListener((m, map) -> {
                        Toast.makeText(requireContext(), m.getTitle() + "\n" + m.getSnippet(), Toast.LENGTH_LONG).show();
                        return true;
                    });
                    osmMap.getOverlays().add(marker);
                }
                osmMap.invalidate();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        osmMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        osmMap.onPause();
    }
}
