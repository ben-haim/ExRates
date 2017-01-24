package com.apps.chernenkovit.ExRates;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.apps.chernenkovit.ExRates.utils.Const.NOTIFICATION_ICON_ID;
import static com.apps.chernenkovit.ExRates.utils.Utils.getNotificationIcon;

/** Service class for hiding notification of foreground class. */
public class HideNotificationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startForeground(NOTIFICATION_ICON_ID, getNotificationIcon(this));
        stopForeground(true);
    }
}