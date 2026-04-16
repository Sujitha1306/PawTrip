package com.pawtrip.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.VH> {

    List<RssItem> list;
    Context ctx;

    public RssAdapter(List<RssItem> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_rss, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RssItem item = list.get(pos);
        h.tvTitle.setText(item.title);
        h.tvSource.setText("📰 " + item.source + "  ·  "
            + (item.pubDate != null
                ? item.pubDate.substring(0, Math.min(10, item.pubDate.length()))
                : ""));
        String desc = item.description != null
            ? item.description.replaceAll("<[^>]*>", "").trim() : "";
        h.tvDesc.setText(desc);

        h.itemView.setOnClickListener(v -> {
            // If it has a real URL, open it; else show full detail dialog
            if (item.link != null && !item.link.isEmpty()
                    && item.link.startsWith("http")) {
                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(item.link)));
            } else {
                showFullDetail(item);
            }
        });
    }

    private void showFullDetail(RssItem item) {
        String fullContent = getFullContent(item.title);
        new AlertDialog.Builder(ctx)
            .setTitle(item.title)
            .setMessage(fullContent)
            .setPositiveButton("Got it ✓", null)
            .show();
    }

    private String getFullContent(String title) {
        if (title.contains("Beaches")) {
            return "🏖️ Top Pet-Friendly Beaches in India\n\n"
                + "1. Goa — Morjim Beach\n   Dogs allowed. Many shacks are pet-friendly.\n\n"
                + "2. Pondicherry — Promenade Beach\n   Early morning walks with pets allowed.\n\n"
                + "3. Kovalam, Kerala\n   Quieter beach, good for dogs to run freely.\n\n"
                + "4. Mandwa Beach, Mumbai\n   Accessible by ferry, less crowded.\n\n"
                + "5. Mahabalipuram, Tamil Nadu\n   Near Chennai, dog-friendly shore temple area.\n\n"
                + "Tips:\n• Carry fresh water — sea water harms dogs\n"
                + "• Avoid peak summer afternoon heat\n"
                + "• Keep leash handy near crowded areas";
        } else if (title.contains("Vet checklist") || title.contains("road trip")) {
            return "🏥 Vet Checklist Before a Road Trip\n\n"
                + "Step 1: Visit your vet 1 week before\n"
                + "• Confirm all vaccinations are current\n"
                + "• Get anti-tick/flea treatment applied\n"
                + "• Ask about motion sickness medication\n\n"
                + "Step 2: Prepare documents\n"
                + "• Vaccination certificate (needed for hotels)\n"
                + "• Pet ID card with your contact number\n"
                + "• Vet's emergency number saved in phone\n\n"
                + "Step 3: Pack pet first aid kit\n"
                + "• Antiseptic wipes\n• Bandage roll\n"
                + "• Tweezers (for ticks)\n• Pet-safe pain relief\n\n"
                + "Step 4: Day of travel\n"
                + "• Feed 2 hours before departure\n"
                + "• Short practice drive the day before\n"
                + "• Bring familiar toy/blanket for comfort";
        } else if (title.contains("train")) {
            return "🚆 Pet-Friendly Train Travel in India\n\n"
                + "Indian Railways Rules:\n"
                + "• Dogs allowed ONLY in AC First Class (1A)\n"
                + "• Small pets in carrier allowed in AC 2-Tier\n"
                + "• No pets in Sleeper or General coaches\n\n"
                + "How to Book:\n"
                + "Step 1: Book your own 1A ticket on IRCTC\n"
                + "Step 2: Go to the Parcel Office at the station\n"
                + "Step 3: Fill Form for 'Dog in Brake Van'\n"
                + "Step 4: Pay pet transport fee (₹30–₹100)\n"
                + "Step 5: Collect receipt and show at boarding\n\n"
                + "What to Carry:\n"
                + "• Vaccination certificate (mandatory)\n"
                + "• Water bowl and food\n"
                + "• Crate or carrier for the journey\n\n"
                + "⚠️ Rules vary — confirm with station master before travel.";
        } else if (title.contains("hotels") || title.contains("Chennai")) {
            return "🏨 Pet-Friendly Hotels in Chennai\n\n"
                + "1. The Leela Palace\n   Pets allowed with deposit. Dedicated walking area.\n\n"
                + "2. Hyatt Regency Chennai\n   Small dogs allowed. Pet sitting on request.\n\n"
                + "3. Taj Coromandel\n   Call ahead to confirm pet policy per room type.\n\n"
                + "4. Bloom Hotel\n   Budget-friendly, known to accommodate pets.\n\n"
                + "Tips when booking:\n"
                + "• Always call the hotel directly to confirm\n"
                + "• Ask about pet deposit (usually ₹500–₹2000)\n"
                + "• Ask which floors are pet-designated\n"
                + "• Carry your pet's vaccination certificate\n"
                + "• Request ground floor for easier outdoor access";
        } else if (title.contains("anxious") || title.contains("calm")) {
            return "😰 How to Calm an Anxious Dog During Travel\n\n"
                + "Before the trip:\n"
                + "• Do 3–4 short practice drives (10 min each)\n"
                + "• Let your dog explore the car when parked\n"
                + "• Place their favourite blanket in the car\n\n"
                + "On the day:\n"
                + "• Feed a light meal 2 hours before leaving\n"
                + "• Play calming music (yes, dogs respond to it)\n"
                + "• Use a Thundershirt if anxiety is severe\n\n"
                + "During the journey:\n"
                + "• Stop every 2 hours for a walk and water\n"
                + "• Never leave pet alone in a hot car\n"
                + "• Talk calmly and reassuringly\n\n"
                + "Vet options:\n"
                + "• Ask your vet about Adaptil spray (pheromone)\n"
                + "• Melatonin (vet-approved dose) for mild anxiety\n"
                + "• Prescription sedatives only for severe cases";
        } else if (title.contains("pack") || title.contains("travel bag")) {
            return "🎒 What to Pack in Your Pet's Travel Bag\n\n"
                + "Food & Water:\n"
                + "• Enough food for the full trip + 1 extra day\n"
                + "• Collapsible water bowl\n"
                + "• Bottled water (avoid unknown water sources)\n\n"
                + "Health & Safety:\n"
                + "• Vaccination certificate\n"
                + "• Regular medications with extra supply\n"
                + "• Tick/flea treatment\n"
                + "• Pet first aid kit\n\n"
                + "Comfort:\n"
                + "• Familiar blanket or toy\n"
                + "• Portable pet bed or mat\n"
                + "• Extra leash and collar with ID tag\n\n"
                + "Hygiene:\n"
                + "• Poop bags (plenty)\n"
                + "• Pet-safe wet wipes\n"
                + "• Grooming brush\n"
                + "• Small towel for paws after walks";
        }
        return "No additional details available.";
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvDesc;
        VH(View v) {
            super(v);
            tvTitle  = v.findViewById(R.id.tvRssTitle);
            tvSource = v.findViewById(R.id.tvRssSource);
            tvDesc   = v.findViewById(R.id.tvRssDesc);
        }
    }
}
