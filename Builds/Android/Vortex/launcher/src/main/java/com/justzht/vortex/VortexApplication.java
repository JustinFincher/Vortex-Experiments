package com.justzht.vortex;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.justzht.unity.lwp.LiveWallpaperManager;
import com.justzht.vortex.manager.AwarenessManager;
import com.justzht.vortex.service.NotificationMessageManager;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

public class VortexApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Sentry.init(new AndroidSentryClientFactory(getApplicationContext()));
        FirebaseApp.initializeApp(VortexApplication.getContext().getApplicationContext());
        NotificationMessageManager.INSTANCE.configNotificationChannel();
        AwarenessManager.INSTANCE.start();
    }

    public static Context getContext()
    {
        return LiveWallpaperManager.getInstance().getContext();
    }

}
