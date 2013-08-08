package com.recursivepenguin.glassdatedisplay;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDisplayService extends Service {

    private WindowManager windowManager;
    private TextView floatingDate;
    BroadcastReceiver broadcastReceiver;

    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Override public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        floatingDate = new TextView(this);
        floatingDate.setText(format.format(new Date()));

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        windowManager.addView(floatingDate, params);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent)
            {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    floatingDate.setText(format.format(new Date()));
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Glass time")
                .getNotification();

        startForeground(123, notification);
    }

    @Override
    public void onDestroy() {

        stopForeground(true);

        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);

        super.onDestroy();
        if (floatingDate != null) windowManager.removeView(floatingDate);
    }

}
