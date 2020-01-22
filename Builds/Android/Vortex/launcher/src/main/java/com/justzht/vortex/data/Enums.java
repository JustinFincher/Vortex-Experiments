package com.justzht.vortex.data;

import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.DetectedActivity;

public class Enums
{
    public enum DetectedActivityType
    {
        IN_VEHICLE,
        ON_BICYCLE,
        ON_FOOT,
        RUNNING,
        STILL,
        TILTING,
        UNKNOWN,
        WALKING
    }

    public static DetectedActivityType getDetectedActivityType(int type)
    {
        switch (type)
        {
            case DetectedActivity.IN_VEHICLE:
                return DetectedActivityType.IN_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return DetectedActivityType.ON_BICYCLE;
            case DetectedActivity.ON_FOOT:
                return DetectedActivityType.ON_FOOT;
            case DetectedActivity.RUNNING:
                return DetectedActivityType.RUNNING;
            case DetectedActivity.STILL:
                return DetectedActivityType.STILL;
            case DetectedActivity.TILTING:
                return DetectedActivityType.TILTING;
            case DetectedActivity.UNKNOWN:
                return DetectedActivityType.UNKNOWN;
            case DetectedActivity.WALKING:
                return DetectedActivityType.WALKING;
            default:
                return DetectedActivityType.UNKNOWN;
        }
    }

    public enum WeatherType
    {
        CLEAR,	//Clear weather condition.
        CLOUDY,	//	Cloudy weather condition.
        FOGGY,	//	Foggy weather condition.
        HAZY,	//	Hazy weather condition.
        ICY,	//	Icy weather condition.
        RAINY,	//	Rainy weather condition.
        SNOWY,	//	Snowy weather condition.
        STORMY,	//	Stormy weather condition.
        UNKNOWN,	//	Unknown weather condition.
        WINDY,	//	Windy weather condition.
    }

    public static WeatherType getWeatherType(int value)
    {
        switch (value)
        {
            case Weather.CONDITION_CLEAR:return WeatherType.CLEAR;
            case Weather.CONDITION_CLOUDY:return WeatherType.CLOUDY;
            case Weather.CONDITION_FOGGY:return WeatherType.FOGGY;
            case Weather.CONDITION_HAZY:return WeatherType.HAZY;
            case Weather.CONDITION_ICY:return WeatherType.ICY;
            case Weather.CONDITION_RAINY:return WeatherType.RAINY;
            case Weather.CONDITION_SNOWY:return WeatherType.SNOWY;
            case Weather.CONDITION_STORMY:return WeatherType.STORMY;
            case Weather.CONDITION_UNKNOWN:return WeatherType.UNKNOWN;
            case Weather.CONDITION_WINDY:return WeatherType.WINDY;
            default:return WeatherType.UNKNOWN;
        }
    }
}
