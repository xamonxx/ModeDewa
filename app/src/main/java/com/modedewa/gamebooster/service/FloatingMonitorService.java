package com.modedewa.gamebooster.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.modedewa.gamebooster.R;
import com.modedewa.gamebooster.model.SystemInfo;
import com.modedewa.gamebooster.ui.HomeActivity;
import com.modedewa.gamebooster.util.DeviceDetector;

/**
 * FloatingMonitorService — Foreground service that shows a persistent notification
 * with real-time system stats while game mode is active.
 */
public class FloatingMonitorService extends Service {

    private static final String CHANNEL_ID = "mode_dewa_monitor";
    private static final int NOTIFICATION_ID = 1001;

    private Handler handler;
    private DeviceDetector deviceDetector;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        deviceDetector = new DeviceDetector(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        Notification notification = buildNotification("Memulai monitoring...");
        startForeground(NOTIFICATION_ID, notification);
        isRunning = true;
        startMonitoring();

        return START_STICKY;
    }

    private void startMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                // Update notification with fresh stats
                SystemInfo info = deviceDetector.getSystemInfo();
                String statsText = String.format(
                        "RAM: %dMB free | Suhu: %s | Baterai: %d%%",
                        info.availableRam,
                        info.getTempDisplay(),
                        info.batteryPercent);

                NotificationManager nm = getSystemService(NotificationManager.class);
                nm.notify(NOTIFICATION_ID, buildNotification(statsText));

                // Repeat every 5 seconds
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }

    private Notification buildNotification(String contentText) {
        Intent openIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingOpen = PendingIntent.getActivity(
                this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, FloatingMonitorService.class);
        stopIntent.setAction("STOP");
        PendingIntent pendingStop = PendingIntent.getService(
                this, 1, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle(getString(R.string.monitor_notification_title))
                .setContentText(contentText)
                .setContentIntent(pendingOpen)
                .addAction(android.R.drawable.ic_delete,
                        "Stop Monitor", pendingStop)
                .setOngoing(true)
                .setSilent(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Game Mode Monitor",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Monitoring performa saat mode dewa aktif");
            channel.setShowBadge(false);
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
