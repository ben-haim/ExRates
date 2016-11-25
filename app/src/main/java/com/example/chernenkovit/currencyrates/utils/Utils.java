package com.example.chernenkovit.currencyrates.utils;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.chernenkovit.currencyrates.R;
import com.example.chernenkovit.currencyrates.UI.MainActivity;

/** Utilities class. */
public class Utils {

    //notification for starting foreground service
    public static Notification getNotificationIcon(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setContentText("Currency Exchange is active")
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        return notification;
    }
}
