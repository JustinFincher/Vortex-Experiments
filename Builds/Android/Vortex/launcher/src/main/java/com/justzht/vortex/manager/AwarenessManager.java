package com.justzht.vortex.manager;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justzht.vortex.data.UnifiedAwarenessData;
import com.justzht.vortex.service.AwarenessRefreshWorker;
import com.unity3d.player.UnityPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.sentry.Sentry;

public enum AwarenessManager
{
    INSTANCE;

    public UnifiedAwarenessData awarenessData = new UnifiedAwarenessData();
    public static String awarenessOneTimeWorkName="AwarenessOneTime";
    public UUID awarenessPeriodicWorkID;
    public static String awarenessPeriodicWorkName="AwarenessPeriodic";
    ObjectMapper mapper = new ObjectMapper();
    Constraints networkConstraint = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    public PublishSubject<Integer> awarenessDataChangedSubject = PublishSubject.create();
    Disposable awarenessDataChangedDebounced;

    private androidx.databinding.Observable.OnPropertyChangedCallback awarenessPropertyChangedCallback = new androidx.databinding.Observable.OnPropertyChangedCallback()
    {
        @Override
        public void onPropertyChanged(androidx.databinding.Observable sender, int propertyId)
        {
            awarenessDataChangedSubject.onNext(propertyId);
        }
    };

    public void start()
    {
        if (awarenessData == null)
        {
            return;
        }
        if (awarenessPeriodicWorkID != null
                &&
                !WorkManager.getInstance().getWorkInfoById(awarenessPeriodicWorkID).isCancelled())
        {
            Log.v("Vortex","awarenessPeriodicWork is still working, do not restart");
            return;
        }
        else
        {
            Log.v("Vortex","awarenessPeriodicWork is starting");
            awarenessData.removeOnPropertyChangedCallback(awarenessPropertyChangedCallback);
            awarenessData.addOnPropertyChangedCallback(awarenessPropertyChangedCallback);

            if (awarenessDataChangedDebounced!= null && !awarenessDataChangedDebounced.isDisposed())
            {
                awarenessDataChangedDebounced.dispose();
            }
            awarenessDataChangedDebounced = awarenessDataChangedSubject.debounce(1, TimeUnit.SECONDS).subscribe(integer ->
            {
                try {
                    SharedPreferencesManager.INSTANCE.SaveUpPreferences(awarenessData);
                    String jsonStr = mapper.writeValueAsString(awarenessData);
                    Log.v("Vortex", "awarenessDataChangedSubject");
                    Log.v("Vortex", jsonStr);
                    UnityPlayer.UnitySendMessage("Manager", "ReceiveAwarenessData", jsonStr);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }, Sentry::capture);

            // one time refresh
            refresh();

            // long time refresh
            PeriodicWorkRequest.Builder periodicWorkRequestBuilder =
                    new PeriodicWorkRequest.Builder(AwarenessRefreshWorker.class, 30,
                            TimeUnit.MINUTES);
            periodicWorkRequestBuilder.setConstraints(networkConstraint);
            PeriodicWorkRequest reriodicWorkRequest = periodicWorkRequestBuilder.build();
            awarenessPeriodicWorkID = reriodicWorkRequest.getId();
            WorkManager.getInstance().enqueueUniquePeriodicWork(awarenessPeriodicWorkName,ExistingPeriodicWorkPolicy.REPLACE,reriodicWorkRequest);
        }
    }

    public void refresh()
    {
        OneTimeWorkRequest.Builder oneTimeWorkRequestBuilder =
                new OneTimeWorkRequest.Builder(AwarenessRefreshWorker.class);
        oneTimeWorkRequestBuilder.setConstraints(networkConstraint);
        WorkManager.getInstance().enqueueUniqueWork(awarenessOneTimeWorkName,ExistingWorkPolicy.REPLACE,oneTimeWorkRequestBuilder.build());
    }

    public void cancelJob()
    {
        WorkManager.getInstance().cancelWorkById(awarenessPeriodicWorkID);
    }
}
