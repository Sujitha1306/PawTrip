package com.pawtrip.app;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.VH> {

    List<Trip> list;
    Context ctx;
    DatabaseHelper db;

    public TripAdapter(List<Trip> list, Context ctx, DatabaseHelper db) {
        this.list = list; this.ctx = ctx; this.db = db;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_trip, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Trip t = list.get(pos);
        h.tvTitle.setText("🧳 " + t.title);
        h.tvRoute.setText(t.origin + " → " + t.destination);
        h.tvDate.setText("📅 " + t.date);
        h.btnEmail.setOnClickListener(v -> sendTripEmail(t));
    }

    private void sendTripEmail(Trip t) {
        String ownerEmail = db.getSetting("owner_email", "");
        if (ownerEmail.isEmpty()) {
            Toast.makeText(ctx, "Set your email in profile first", Toast.LENGTH_SHORT).show();
            return;
        }
        String body = "PawTrip Itinerary\n\n"
            + "Trip: " + t.title + "\n"
            + "Route: " + t.origin + " → " + t.destination + "\n"
            + "Date: " + t.date + "\n"
            + "Notes: " + t.notes + "\n\n"
            + "Pet Health Checklist:\n"
            + "□ Vaccination records packed\n"
            + "□ Vet contact saved\n"
            + "□ Food and water supply\n"
            + "□ Pet ID tag attached\n"
            + "□ First aid kit ready\n\n"
            + "Have a great trip! 🐾";
        EmailSender.sendEmail(ownerEmail, "PawTrip: " + t.title + " Itinerary", body, ctx);
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRoute, tvDate;
        Button btnEmail;
        VH(View v) {
            super(v);
            tvTitle  = v.findViewById(R.id.tvTripTitle);
            tvRoute  = v.findViewById(R.id.tvTripRoute);
            tvDate   = v.findViewById(R.id.tvTripDate);
            btnEmail = v.findViewById(R.id.btnEmailTrip);
        }
    }
}