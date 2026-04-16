package com.pawtrip.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import java.util.List;

public class TripsFragment extends Fragment {

    RecyclerView rvTrips;
    TripAdapter adapter;
    List<Trip> trips;
    DatabaseHelper db;

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
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.show();
    }
}