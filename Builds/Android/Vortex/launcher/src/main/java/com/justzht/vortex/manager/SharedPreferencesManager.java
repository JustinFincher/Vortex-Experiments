package com.justzht.vortex.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.justzht.vortex.VortexApplication;
import com.justzht.vortex.data.SharedPreferencesAnnotation;
import com.justzht.vortex.data.UnifiedAwarenessData;

import java.util.stream.Stream;

public enum  SharedPreferencesManager
{
    INSTANCE;

    public void LoadUpPreferences(UnifiedAwarenessData unifiedAwarenessData)
    {
        Log.v("Vortex","LoadUpPreferences");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VortexApplication.getContext());
        Class awarenessDataClass = unifiedAwarenessData.getClass();
        Stream.of(awarenessDataClass.getFields()).filter(field -> field.isAnnotationPresent(SharedPreferencesAnnotation.class)).forEach(field ->
        {
            SharedPreferencesAnnotation sharedPreferencesAnnotation = field.getAnnotation(SharedPreferencesAnnotation.class);
            try
            {
                if (field.getType() == boolean.class)
                {
                    boolean value = sharedPreferences.getBoolean(sharedPreferencesAnnotation.path(),Boolean.parseBoolean(sharedPreferencesAnnotation.defaultValue()));
                    field.set(unifiedAwarenessData, value);
                    Log.v("Vortex",unifiedAwarenessData + " Get Boolean KEY " + sharedPreferencesAnnotation.path() + " VALUE " + String.valueOf(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        unifiedAwarenessData.notifyChange();
    }

    public void SaveUpPreferences(UnifiedAwarenessData unifiedAwarenessData)
    {
        Log.v("Vortex","SaveUpPreferences");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(VortexApplication.getContext());
        Class awarenessDataClass = unifiedAwarenessData.getClass();
        Stream.of(awarenessDataClass.getFields()).filter(field -> field.isAnnotationPresent(SharedPreferencesAnnotation.class)).forEach(field ->
        {
            SharedPreferencesAnnotation sharedPreferencesAnnotation = field.getAnnotation(SharedPreferencesAnnotation.class);
            try
            {
                if (field.getType() == boolean.class)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(sharedPreferencesAnnotation.path(), field.getBoolean(unifiedAwarenessData));
                    editor.apply();
                    Log.v("Vortex",unifiedAwarenessData + " Save Boolean KEY " + sharedPreferencesAnnotation.path() + " VALUE " + field.getBoolean(unifiedAwarenessData));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
