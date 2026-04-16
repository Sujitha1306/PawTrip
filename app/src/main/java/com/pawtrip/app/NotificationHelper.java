package com.pawtrip.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "pawtrip_channel";

    public static void createChannel(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "PawTrip Alerts",
                NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Pet travel notifications");
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