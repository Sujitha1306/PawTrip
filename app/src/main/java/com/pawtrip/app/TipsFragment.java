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
    android.widget.LinearLayout llTipsLoading;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    // Reliable free RSS feeds (pet / dog news)
    private static final String[] RSS_URLS = {
        "https://www.akc.org/rss/news/",
        "https://feeds.feedburner.com/dogtime-dogs"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle s) {
        View v = inflater.inflate(R.layout.fragment_tips, container, false);
        rvRss         = v.findViewById(R.id.rvRss);
        progressBar   = v.findViewById(R.id.progressBar);
        llTipsLoading = v.findViewById(R.id.llTipsLoading);
        rvRss.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetchRss();
        return v;
    }

    private void fetchRss() {
        if (llTipsLoading != null) llTipsLoading.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            List<RssItem> items = new ArrayList<>();
            for (String url : RSS_URLS) {
                try {
                    java.io.InputStream is = new URL(url).openStream();
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
                                if ("title".equals(tag))       current.title       = parser.nextText();
                                else if ("link".equals(tag))  current.link        = parser.nextText();
                                else if ("description".equals(tag)) current.description = parser.nextText();
                                else if ("pubDate".equals(tag)) current.pubDate   = parser.nextText();
                            }
                        } else if (event == XmlPullParser.END_TAG && "item".equals(tag) && current != null) {
                            current.source = url.contains("akc") ? "AKC.org" : "DogTime.com";
                            items.add(current);
                            current = null;
                        }
                        event = parser.next();
                    }
                } catch (Exception e) {
                    // Feed unavailable, continue to next or fallback
                }
            }

            // Offline fallback — curated tips with real website links
            if (items.isEmpty()) {
                Object[][] tips = {
                    {"Top Pet-Friendly Beaches in India 🏖️",
                     "Goa, Pondicherry, Kovalam — beaches where your dog can run free on the sand.",
                     "CarryMyPet.com", "2025-01-01",
                     "https://www.carrymypet.com/pet-friendly-beaches-in-india-best-beaches-to-visit-with-your-dog"},
                    {"Vet Checklist Before a Road Trip 🚗",
                     "Vaccinations, tick treatment, travel anxiety meds — what to confirm before leaving.",
                     "TurnerVet.com", "2025-01-05",
                     "https://turnervet.com/blog?article_id=road-trip-ready-how-to-travel-safely-with-your-pet-this-summer"},
                    {"Travelling with Your Dog via Indian Railways 🚆",
                     "IR allows dogs in AC First Class with a valid ticket. Here's how to book.",
                     "HeadsUpForTails.com", "2025-01-10",
                     "https://headsupfortails.com/blogs/dogs/travelling-with-your-dog-via-indian-railways"},
                    {"Best Pet-Friendly Hotels in Chennai 🏨",
                     "Hotels in Chennai with dedicated pet policies, bowls, and walking areas.",
                     "Booking.com", "2025-01-15",
                     "https://www.booking.com/pets/city/in/chennai.en-gb.html"},
                    {"Say Goodbye to Pet Anxiety During Travel 🙏",
                     "Thundershirts, familiar toys, and short practice drives all help reduce stress.",
                     "AMTM India", "2025-01-20",
                     "https://amtmindia.org/say-goodbye-to-pet-anxiety-tips-for-soothing-separation-stress/"},
                    {"Ultimate Dog Packing List 🎒",
                     "Food, water bowl, leash, poop bags, first aid kit, vaccination records.",
                     "EagleCreek.com", "2025-01-25",
                     "https://eaglecreek.com/blogs/articles/ultimate-dog-packing-list-what-to-pack-for-your-pet"}
                };
                for (Object[] tip : tips) {
                    RssItem item = new RssItem();
                    item.title       = (String) tip[0];
                    item.description = (String) tip[1];
                    item.source      = (String) tip[2];
                    item.pubDate     = (String) tip[3];
                    item.link        = (String) tip[4];   // real URL — opens in browser
                    items.add(item);
                }
            }

            mainHandler.post(() -> {
                if (isAdded()) {
                    if (llTipsLoading != null) llTipsLoading.setVisibility(View.GONE);
                    rvRss.setAdapter(new RssAdapter(items, requireContext()));
                }
            });
        });
    }
}
