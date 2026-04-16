package com.pawtrip.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import org.xmlpull.v1.*;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class TipsFragment extends Fragment {

    RecyclerView rvRss;
    ProgressBar progressBar;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    // Pet-related RSS feeds (free, no auth needed)
    private static final String[] RSS_URLS = {
        "https://www.petmd.com/rss.xml",
        "https://bringfido.com/blog/feed/"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_tips, container, false);
        rvRss = v.findViewById(R.id.rvRss);
        progressBar = v.findViewById(R.id.progressBar);
        rvRss.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetchRss();
        return v;
    }

    private void fetchRss() {
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            List<RssItem> items = new ArrayList<>();
            for (String url : RSS_URLS) {
                try {
                    InputStream is = new URL(url).openStream();
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(is, "UTF-8");
                    RssItem current = null;
                    int event = parser.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {
                        String tag = parser.getName();
                        if (event == XmlPullParser.START_TAG) {
                            if ("item".equals(tag)) current = new RssItem();
                            else if (current != null) {
                                if ("title".equals(tag)) current.title = parser.nextText();
                                else if ("link".equals(tag)) current.link = parser.nextText();
                                else if ("description".equals(tag)) current.description = parser.nextText();
                                else if ("pubDate".equals(tag)) current.pubDate = parser.nextText();
                            }
                        } else if (event == XmlPullParser.END_TAG && "item".equals(tag) && current != null) {
                            current.source = url.contains("petmd") ? "PetMD" : "BringFido";
                            items.add(current);
                            current = null;
                        }
                        event = parser.next();
                    }
                } catch (Exception e) {
                    // Feed unavailable, continue
                }
            }
            // Add offline fallback items if empty
            if (items.isEmpty()) {
                String[][] tips = {
                    {"Top 10 Pet-Friendly Beaches in India",
                     "Goa, Pondicherry, Kovalam — beaches where your dog can run free on the sand.",
                     "PawTrip Tips", "2025-01-01"},
                    {"Vet checklist before a road trip",
                     "Vaccinations, tick treatment, travel anxiety meds — what to confirm before leaving.",
                     "PawTrip Tips", "2025-01-05"},
                    {"Pet-friendly trains in India — full guide",
                     "IR allows dogs in AC First Class with a valid ticket. Here's how to book.",
                     "PawTrip Tips", "2025-01-10"},
                    {"Best pet-friendly hotels in Chennai",
                     "These Chennai hotels have dedicated pet policies, bowls, and walking areas.",
                     "PawTrip Tips", "2025-01-15"},
                    {"How to calm an anxious dog during travel",
                     "Thundershirts, familiar toys, and short practice drives all help reduce stress.",
                     "PawTrip Tips", "2025-01-20"},
                    {"What to pack in your pet's travel bag",
                     "Food, water bowl, leash, poop bags, first aid kit, vaccination records.",
                     "PawTrip Tips", "2025-01-25"}
                };
                for (String[] tip : tips) {
                    RssItem item = new RssItem();
                    item.title = tip[0];
                    item.description = tip[1];
                    item.source = tip[2];
                    item.pubDate = tip[3];
                    items.add(item);
                }
            }
            mainHandler.post(() -> {
                if (isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    rvRss.setAdapter(new RssAdapter(items, requireContext()));
                }
            });
        });
    }
}
