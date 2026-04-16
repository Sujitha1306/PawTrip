package com.pawtrip.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String title   = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        NotificationHelper.sendNotification(ctx, (int) System.currentTimeMillis(), title, message);
    }
}