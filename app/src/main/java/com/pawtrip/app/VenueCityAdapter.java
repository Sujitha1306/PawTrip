package com.pawtrip.app;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class VenueCityAdapter extends RecyclerView.Adapter<VenueCityAdapter.VH> {

    private final Map<String, List<Venue>> cityMap;
    private final List<String> cities;
    private final Set<String> expandedCities = new HashSet<>();
    private final Context ctx;
    private final VenueAdapter.OnVenueClick listener;

    public VenueCityAdapter(List<Venue> all, Context ctx,
            VenueAdapter.OnVenueClick listener) {
        this.ctx = ctx;
        this.listener = listener;
        cityMap = new LinkedHashMap<>();
        for (Venue v : all) {
            String city = v.city != null ? v.city : "Other";
            if (!cityMap.containsKey(city)) cityMap.put(city, new ArrayList<>());
            cityMap.get(city).add(v);
        }
        cities = new ArrayList<>(cityMap.keySet());
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(
            R.layout.item_city_group, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String city = cities.get(pos);
        List<Venue> venues = cityMap.get(city);
        boolean expanded = expandedCities.contains(city);

        h.tvCityName.setText(getCityEmoji(city) + "  " + city);
        h.tvCount.setText(venues.size() + " venues");
        h.ivArrow.setText(expanded ? "▲" : "▼");
        h.llVenues.setVisibility(expanded ? View.VISIBLE : View.GONE);

        h.llVenues.removeAllViews();
        if (expanded) {
            for (Venue venue : venues) {
                View vv = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_venue_small, h.llVenues, false);
                TextView tvName  = vv.findViewById(R.id.tvSmallName);
                TextView tvScore = vv.findViewById(R.id.tvSmallScore);
                TextView tvHours = vv.findViewById(R.id.tvSmallHours);
                tvName.setText(venue.getTypeEmoji() + "  " + venue.name);
                tvScore.setText("🐾 " + venue.pawScore);
                tvHours.setText("🕐 " + venue.openHours);
                vv.setOnClickListener(x -> listener.onClick(venue));
                h.llVenues.addView(vv);
            }
        }

        h.cardCity.setOnClickListener(x -> {
            if (expanded) expandedCities.remove(city);
            else expandedCities.add(city);
            notifyItemChanged(pos);
        });
    }

    private String getCityEmoji(String city) {
        switch (city) {
            case "Chennai":     return "🌊";
            case "Coimbatore":  return "🏔️";
            case "Bangalore":   return "🌆";
            case "Mumbai":      return "🏙️";
            case "Pondicherry": return "🏖️";
            default:            return "📍";
        }
    }

    @Override public int getItemCount() { return cities.size(); }

    static class VH extends RecyclerView.ViewHolder {
        androidx.cardview.widget.CardView cardCity;
        TextView tvCityName, tvCount, ivArrow;
        LinearLayout llVenues;
        VH(View v) {
            super(v);
            cardCity   = v.findViewById(R.id.cardCity);
            tvCityName = v.findViewById(R.id.tvCityName);
            tvCount    = v.findViewById(R.id.tvVenueCount);
            ivArrow    = v.findViewById(R.id.tvArrow);
            llVenues   = v.findViewById(R.id.llVenueList);
        }
    }
}
