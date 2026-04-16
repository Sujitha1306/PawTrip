package com.pawtrip.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID     = "pawtrip_channel";
    public static final String CHANNEL_TRIPS  = "pawtrip_trips_v2";

    public static void createChannel(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "PawTrip Alerts",
                NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Pet travel notifications");
            ch.enableVibration(true);
            ch.enableLights(true);
            ctx.getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    public static void createTripsChannel(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_TRIPS, "PawTrip Trip Alerts",
                NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Notifies when a trip is saved");
            ch.enableVibration(true);
            ch.enableLights(true);
            ch.setShowBadge(true);
            ctx.getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    public static void sendNotification(Context ctx, int id, String title, String text) {
        createChannel(ctx);
        Notification n = new NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build();
        ctx.getSystemService(NotificationManager.class).notify(id, n);
    }

    /**
     * Fires a heads-up notification when a new trip is saved.
     * Tapping it opens MainActivity and navigates to the Trips tab.
     *
     * @param ctx         any context
     * @param destination the trip destination string shown in the body
     */
    public static void sendTripSavedNotification(Context ctx, String destination) {
        createTripsChannel(ctx);

        // Tapping the notification opens MainActivity on the Trips tab
        Intent openTrips = new Intent(ctx, MainActivity.class);
        openTrips.putExtra("open_tab", R.id.nav_trips);
        openTrips.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        android.app.PendingIntent pi = android.app.PendingIntent.getActivity(
            ctx, 2001, openTrips,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT
                | android.app.PendingIntent.FLAG_IMMUTABLE);

        String title = "🧳 Trip Saved!";
        String body  = destination != null && !destination.isEmpty()
            ? "Trip to " + destination + " saved! We'll keep your pet details ready. 🐾"
            : "New trip saved! We'll keep your pet details ready. 🐾";

        Notification n = new NotificationCompat.Builder(ctx, CHANNEL_TRIPS)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_MAX)   // MAX forces heads-up
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(new long[]{0, 300, 100, 300})       // explicit vibrate pattern
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build();

        ctx.getSystemService(NotificationManager.class).notify(2001, n);
    }

    public static void scheduleVetReminder(Context ctx, String message, int delaySeconds) {
        Intent intent = new Intent(ctx, NotificationReceiver.class);
        intent.putExtra("title", "🏥 PawTrip Vet Reminder");
        intent.putExtra("message", message);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + delaySeconds * 1000L, pi);
        }
    }
}