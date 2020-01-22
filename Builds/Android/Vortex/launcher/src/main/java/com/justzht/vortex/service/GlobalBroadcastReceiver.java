package com.justzht.vortex.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.justzht.vortex.VortexApplication;
import com.justzht.vortex.data.Values;
import com.justzht.vortex.manager.AwarenessManager;

import androidx.core.app.NotificationManagerCompat;

public class GlobalBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction() == null)
        {

        }else if (intent.getAction().equals(Values.ActionRefreshAwareness))
        {
            Log.v("Vortex",Values.ActionRefreshAwareness);
            int notificationID = intent.getIntExtra(Values.ExtraNotificationID,0);
            if (notificationID != 0)
            {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(VortexApplication.getContext());
                notificationManager.cancel(notificationID);
            }
            Toast.makeText(context, "Refreshing Awareness Data", Toast.LENGTH_SHORT).show();
            AwarenessManager.INSTANCE.refresh();
        }
    }
}
