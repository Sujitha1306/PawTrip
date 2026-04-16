package com.pawtrip.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import java.util.List;

public class TripsFragment extends Fragment {

    private static final int NOTIF_PERM_CODE = 201;

    RecyclerView rvTrips;
    TripAdapter adapter;
    List<Trip> trips;
    DatabaseHelper db;
    // Holds the destination while we wait for permission grant
    private String pendingNotifDestination = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_trips, container, false);
        db = new DatabaseHelper(requireContext());
        trips = db.getAllTrips();

        rvTrips = v.findViewById(R.id.rvTrips);
        rvTrips.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TripAdapter(trips, requireContext(), db);
        rvTrips.setAdapter(adapter);

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r,
                    @NonNull RecyclerView.ViewHolder v,
                    @NonNull RecyclerView.ViewHolder t) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int dir) {
                int pos = viewHolder.getAdapterPosition();
                Trip t  = trips.get(pos);
                new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Trip?")
                    .setMessage("Delete \"" + t.title + "\"?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.deleteTrip(t.id);
                        trips.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        Toast.makeText(requireContext(),
                            "Trip deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (d, w) ->
                        adapter.notifyItemChanged(pos))
                    .show();
            }
        }).attachToRecyclerView(rvTrips);

        v.findViewById(R.id.fabAddTrip).setOnClickListener(x -> showAddTripDialog());
        return v;
    }

    private void showAddTripDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_trip, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("🧳 Plan New Trip")
            .setPositiveButton("Save", (d, which) -> {
                EditText etTitle  = dialogView.findViewById(R.id.etTripTitle);
                EditText etOrigin = dialogView.findViewById(R.id.etOrigin);
                EditText etDest   = dialogView.findViewById(R.id.etDestination);
                EditText etDate   = dialogView.findViewById(R.id.etDate);
                EditText etNotes  = dialogView.findViewById(R.id.etTripNotes);

                if (etTitle.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show();
                    return;
                }
                Trip t = new Trip();
                t.title = etTitle.getText().toString();
                t.origin = etOrigin.getText().toString();
                t.destination = etDest.getText().toString();
                t.date = etDate.getText().toString();
                t.notes = etNotes.getText().toString();
                db.insertTrip(t);
                trips.clear();
                trips.addAll(db.getAllTrips());
                adapter.notifyDataSetChanged();
                // Fire trip-saved heads-up notification (requests permission on API 33+)
                String dest = t.destination.isEmpty() ? t.title : t.destination;
                fireNotificationWithPermission(dest);
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }

    /**
     * On Android 13+ (API 33) POST_NOTIFICATIONS must be granted at runtime.
     * This method checks the grant state and either fires immediately or asks.
     */
    private void fireNotificationWithPermission(String destination) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33+: need runtime permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Already granted — fire straight away
                NotificationHelper.sendTripSavedNotification(requireContext(), destination);
            } else {
                // Not granted — save destination and ask the user
                pendingNotifDestination = destination;
                requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIF_PERM_CODE);
            }
        } else {
            // Below API 33 — no runtime permission needed
            NotificationHelper.sendTripSavedNotification(requireContext(), destination);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIF_PERM_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User just granted — fire the pending notification
                if (pendingNotifDestination != null) {
                    NotificationHelper.sendTripSavedNotification(
                        requireContext(), pendingNotifDestination);
                    pendingNotifDestination = null;
                }
            } else {
                Toast.makeText(requireContext(),
                    "Allow notifications so PawTrip can alert you when trips are saved.",
                    Toast.LENGTH_LONG).show();
            }
        }
    }
}