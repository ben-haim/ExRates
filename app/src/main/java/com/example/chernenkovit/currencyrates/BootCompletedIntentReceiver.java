package com.example.chernenkovit.currencyrates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//receiver for cellphone boot listening for service restarting
public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, UpdateService.class);
            context.startService(pushIntent);
        }
    }
}
