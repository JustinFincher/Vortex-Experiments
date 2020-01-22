package com.justzht.vortex.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Tasks;
import com.justzht.unity.lwp.LiveWallpaperManager;
import com.justzht.vortex.BR;
import com.justzht.vortex.BuildConfig;
import com.justzht.vortex.R;
import com.justzht.vortex.VortexApplication;
import com.justzht.vortex.activity.UnityPlayerActivity;
import com.justzht.vortex.data.Enums;
import com.justzht.vortex.data.Values;
import com.justzht.vortex.helper.Utils;
import com.justzht.vortex.manager.AwarenessManager;
import com.justzht.vortex.manager.SharedPreferencesManager;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import io.sentry.Sentry;

public class AwarenessRefreshWorker extends Worker
{
    private SnapshotClient snapshotClient;

    public AwarenessRefreshWorker(@NonNull Context context, @NonNull WorkerParameters workerParams)
    {
        super(context, workerParams);
    }

    @SuppressLint("MissingPermission")
    @Override
    @NonNull
    public Result doWork()
    {
        if (!LiveWallpaperManager.getInstance().isWallpaperSet() && !LiveWallpaperManager.getInstance().isUnityDisplaying())
        {
            // todo
        }
        if (snapshotClient == null)
        {
            snapshotClient = Awareness.getSnapshotClient(VortexApplication.getContext());
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(VortexApplication.getContext());
        notificationManager.cancel(AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode());

        if (Utils.permissionsGranted())
        {
            if (!Utils.isGMSAvailable())
            {
                if (Utils.isGMSResolvable())
                {
                    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
                    apiAvailability.showErrorNotification(VortexApplication.getContext(),Utils.getGMSResultCode());

                }else
                {
                    Intent intent = new Intent(VortexApplication.getContext(), UnityPlayerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setAction(Values.ActionRequestPermissions);
                    PendingIntent pendingIntent = PendingIntent.getActivity(VortexApplication.getContext(), 0, intent, 0);

                    NotificationCompat.Builder mBuilder = NotificationMessageManager.INSTANCE.getDefaultBuilder()
                            .setContentTitle("Google Play Service Missing")
                            .setContentText("Vortex need Play Service to work")
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setCategory(NotificationCompat.CATEGORY_ERROR);
                    notificationManager.notify(AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode(), mBuilder.build());

                }

                return Result.failure();
            }
            try {
                WeatherResponse weatherResponse = Tasks.await(snapshotClient.getWeather());
                LocationResponse locationResponse = Tasks.await(snapshotClient.getLocation());
                DetectedActivityResponse detectedActivityResponse = Tasks.await(snapshotClient.getDetectedActivity());

                if (weatherResponse.getWeather() != null)
                {
                    AwarenessManager.INSTANCE.awarenessData.feelsLikeTemperatureInCelsius = Float.valueOf(String.format(Locale.US,"%.2f",weatherResponse.getWeather().getFeelsLikeTemperature(Weather.CELSIUS)));
                    AwarenessManager.INSTANCE.awarenessData.feelsLikeTemperatureInFahrenheit = Float.valueOf(String.format(Locale.US,"%.2f",weatherResponse.getWeather().getFeelsLikeTemperature(Weather.FAHRENHEIT)));
                    AwarenessManager.INSTANCE.awarenessData.humidity = weatherResponse.getWeather().getHumidity();
                    AwarenessManager.INSTANCE.awarenessData.weatherTypes = Arrays.stream(weatherResponse.getWeather().getConditions()).mapToObj(Enums::getWeatherType).collect(Collectors.toSet());

                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.feelsLikeTemperatureInCelsius);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.feelsLikeTemperatureInFahrenheit);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.humidity);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.weatherTypes);
                }

                if (locationResponse.getLocation() != null)
                {
                    AwarenessManager.INSTANCE.awarenessData.longitude = locationResponse.getLocation().getLongitude();
                    AwarenessManager.INSTANCE.awarenessData.latitude =  locationResponse.getLocation().getLatitude();

                    SunriseSunsetCalculator sunriseSunsetCalculator = new SunriseSunsetCalculator(new Location(AwarenessManager.INSTANCE.awarenessData.latitude,AwarenessManager.INSTANCE.awarenessData.longitude),java.util.TimeZone.getDefault());
                    AwarenessManager.INSTANCE.awarenessData.dayTime = sunriseSunsetCalculator.getAstronomicalSunriseForDate(Calendar.getInstance());
                    AwarenessManager.INSTANCE.awarenessData.nightTime = sunriseSunsetCalculator.getAstronomicalSunsetForDate(Calendar.getInstance());

                    DateTime nowDateTime = new DateTime();
                    AwarenessManager.INSTANCE.awarenessData.currentTime = String.format(Locale.US,"%02d", nowDateTime.getHourOfDay()) + ":" + String.format(Locale.US,"%02d", nowDateTime.getMinuteOfHour());

                    Log.v("Vortex","NowDateTime " + nowDateTime.toString());

                    DateTime sunriseDateTime = nowDateTime
                            .withHourOfDay(Integer.parseInt(AwarenessManager.INSTANCE.awarenessData.dayTime.split(":")[0]))
                            .withMinuteOfHour(Integer.parseInt(AwarenessManager.INSTANCE.awarenessData.dayTime.split(":")[1]));

                    Log.v("Vortex","SunriseDateTime " + sunriseDateTime.toString());

                    DateTime sunsetDateTime = nowDateTime
                            .withHourOfDay(Integer.parseInt(AwarenessManager.INSTANCE.awarenessData.nightTime.split(":")[0]))
                            .withMinuteOfHour(Integer.parseInt(AwarenessManager.INSTANCE.awarenessData.nightTime.split(":")[1]));

                    Log.v("Vortex","SunsetDateTime " + sunsetDateTime.toString());

                    float progress = 0;
                    if (nowDateTime.isAfter(sunsetDateTime))
                    {
                        progress =
                                (float) (sunriseDateTime.getMillis() - nowDateTime.plusDays(-1).getMillis())
                                        /
                                        (float) (sunriseDateTime.getMillis() - sunsetDateTime.plusDays(-1).getMillis());
                        progress = progress - 1;
                    }else
                    {
                        progress =
                                (float) (nowDateTime.getMillis() - sunriseDateTime.getMillis())
                                        /
                                        (float) (sunsetDateTime.getMillis() - sunriseDateTime.getMillis());
                    }

                    //            sunrise     sunset
                    // sunset --- sunrise --- sunset
                    // -1     --- 0       --- 1

                    AwarenessManager.INSTANCE.awarenessData.dayNightProgress = progress;

                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.longitude);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.latitude);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.currentTime);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.dayTime);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.dayNightProgress);
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.nightTime);
                }

                SharedPreferencesManager.INSTANCE.LoadUpPreferences(AwarenessManager.INSTANCE.awarenessData);
                AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.isTimelapse);

                if (detectedActivityResponse.getActivityRecognitionResult() != null)
                {
                    AwarenessManager.INSTANCE.awarenessData.detectedActivityType = Enums.getDetectedActivityType(detectedActivityResponse.getActivityRecognitionResult().getMostProbableActivity().getType());
                    AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.detectedActivityType);
                }

                AwarenessManager.INSTANCE.awarenessData.isDebugMode = BuildConfig.DEBUG;
                AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.isDebugMode);
                if (BuildConfig.DEBUG)
                {
                    NotificationCompat.Builder mBuilder =
                            NotificationMessageManager.INSTANCE.getDefaultBuilder()
                                    .setContentTitle("Awareness API Fetch Success")
                                    .setContentText("Results Below")
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Awareness Data: "
                                                    + "\nCoordinate " + AwarenessManager.INSTANCE.awarenessData.latitude + "," +  AwarenessManager.INSTANCE.awarenessData.longitude
                                                    + "\nTemperature " + AwarenessManager.INSTANCE.awarenessData.feelsLikeTemperatureInCelsius
                                                    + "\nHumidity " + AwarenessManager.INSTANCE.awarenessData.humidity
                                                    + "\nSunrise / Sunset " + AwarenessManager.INSTANCE.awarenessData.dayTime + " - " + AwarenessManager.INSTANCE.awarenessData.nightTime
                                                    + "\nWallpaper Set " +  LiveWallpaperManager.getInstance().isWallpaperSet()
                                                    + "\nRefreshed " + AwarenessManager.INSTANCE.awarenessData.currentTime
                                                    + "\nProgress " + AwarenessManager.INSTANCE.awarenessData.dayNightProgress
                                                    + "\nActivity " + AwarenessManager.INSTANCE.awarenessData.detectedActivityType.name()
                                                    + "\nWeather " + AwarenessManager.INSTANCE.awarenessData.weatherTypes.stream().map(Enum::name).collect( Collectors.joining( " | " ) )
                                            ))
                                    .setPriority(NotificationCompat.PRIORITY_MIN)
                                    .setCategory(NotificationCompat.CATEGORY_SERVICE);
                    notificationManager.notify(AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode(), mBuilder.build());
                }

                return Result.success();
            } catch (Exception e)
            {
                Sentry.capture(e);
                Intent refreshIntent = new Intent(VortexApplication.getContext(), GlobalBroadcastReceiver.class);
                refreshIntent.setAction(Values.ActionRefreshAwareness);
                refreshIntent.putExtra(Values.ExtraNotificationID,AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode());
                PendingIntent refreshPendingIntent =
                        PendingIntent.getBroadcast(VortexApplication.getContext(), 0, refreshIntent, 0);

                NotificationCompat.Builder mBuilder = NotificationMessageManager.INSTANCE.getDefaultBuilder()
                        .setContentTitle("Awareness API Fetch Failed")
                        .setContentText("Will try in the next cycle")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(
                                        "ERROR:\n" + e.getMessage()
                                ))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setCategory(NotificationCompat.CATEGORY_ERROR)
                        .addAction(R.drawable.ic_baseline_refresh_24px, "Refresh Again",
                                refreshPendingIntent);
                notificationManager.notify(AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode(), mBuilder.build());
                e.printStackTrace();
                return Result.retry();
            }
        }else
        {

            Intent intent = new Intent(VortexApplication.getContext(), UnityPlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(Values.ActionRequestPermissions);
            PendingIntent pendingIntent = PendingIntent.getActivity(VortexApplication.getContext(), 0, intent, 0);

            NotificationCompat.Builder mBuilder = NotificationMessageManager.INSTANCE.getDefaultBuilder()
                    .setContentTitle("Permissions Needed")
                    .setContentText("This data-driven app won't work properly until permission granted. Please tap to solve.")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_ERROR);
            notificationManager.notify(AwarenessManager.INSTANCE.awarenessPeriodicWorkID.hashCode(), mBuilder.build());
            return Result.failure();
        }
    }
}