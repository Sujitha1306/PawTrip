package com.pawtrip.app;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.VH> {

    public interface OnVenueClick { void onClick(Venue venue); }

    List<Venue> list;
    Context ctx;
    OnVenueClick listener;

    public VenueAdapter(List<Venue> list, Context ctx, OnVenueClick listener) {
        this.list = list;
        this.ctx = ctx;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_venue, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Venue v = list.get(pos);
        h.tvName.setText(v.getTypeEmoji() + "  " + v.name);
        h.tvType.setText(v.type.substring(0,1).toUpperCase() + v.type.substring(1));
        h.tvHours.setText("🕐 " + v.openHours);
        h.tvScore.setText("🐾 " + v.pawScore);
        h.tvRules.setText(v.petRules);
        h.itemView.setOnClickListener(x -> {
            if (listener != null) listener.onClick(v);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvHours, tvScore, tvRules;
        VH(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tvVenueName);
            tvType  = v.findViewById(R.id.tvVenueType);
            tvHours = v.findViewById(R.id.tvVenueHours);
            tvScore = v.findViewById(R.id.tvPawScore);
            tvRules = v.findViewById(R.id.tvPetRules);
        }
    }
}