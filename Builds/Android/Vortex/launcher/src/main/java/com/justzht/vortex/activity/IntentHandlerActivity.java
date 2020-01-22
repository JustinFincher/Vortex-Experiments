package com.justzht.vortex.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.justzht.vortex.VortexApplication;
import com.justzht.vortex.service.GlobalBroadcastReceiver;

public class IntentHandlerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if (intent != null)
        {
            Intent repackageIntent = new Intent(VortexApplication.getContext(), GlobalBroadcastReceiver.class);
            repackageIntent.setAction(intent.getAction());
            VortexApplication.getContext().sendBroadcast(repackageIntent);
        }
        finishAndRemoveTask();
    }
}
