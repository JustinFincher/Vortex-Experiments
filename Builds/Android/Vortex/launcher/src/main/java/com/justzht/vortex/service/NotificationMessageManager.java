package com.justzht.vortex.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.justzht.vortex.R;
import com.justzht.vortex.VortexApplication;
import com.justzht.vortex.manager.AwarenessManager;

import androidx.core.app.NotificationCompat;

public enum NotificationMessageManager
{
    INSTANCE;

    public static String channel = "Vortex";

    public void configNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = channel;
            String description = channel;
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationMessageManager.channel, name, importance);
            channel.enableVibration(false);
            channel.setImportance(NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            android.app.NotificationManager notificationManager = VortexApplication.getContext().getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public NotificationCompat.Builder getDefaultBuilder()
    {
        return  new NotificationCompat.Builder(VortexApplication.getContext(), channel)
                .setSmallIcon(R.drawable.ic_app_icon_black_extra_svg)
                .setAutoCancel(true);
    }
}
