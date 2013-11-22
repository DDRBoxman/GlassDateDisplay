package com.recursivepenguin.glassdatedisplay;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DateDisplayService extends Service {

    private WindowManager windowManager;
    private TextView floatingDate;
    BroadcastReceiver broadcastReceiver;

    Timer timer;

    Handler handler;

    @Override public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    @Override public void onCreate() {
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());

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

        scehduleFadeout();

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofFloat(floatingDate, "alpha", 1f).start();
                        scehduleFadeout();
                    }
                });
            }
        }, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    private void scehduleFadeout() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator.ofFloat(floatingDate, "alpha", 0f).start();
                    }
                });
            }
        }, 3500);
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
