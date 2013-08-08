package com.recursivepenguin.glassdatedisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, DateDisplayService.class);
        context.startService(myIntent);
    }
}
