package com.justzht.vortex.data;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifiedAwarenessData extends BaseObservable
{
        @JsonProperty("temp")
        @Bindable
        public float feelsLikeTemperatureInCelsius = 0;

        @JsonIgnore
        @Bindable
        public float feelsLikeTemperatureInFahrenheit = 0;

        @JsonProperty("hum")
        @Bindable
        public float humidity = 0;

        @JsonProperty("lon")
        @Bindable
        public double longitude = 0;
        @JsonProperty("lat")
        @Bindable
        public double latitude = 0;

        @JsonProperty("progress")
        @Bindable
        public float dayNightProgress = 0; // -1 to 1, -1 is sunset, 0 is sunrise, 1 is sunset

        @JsonProperty("day")
        @Bindable
        public String dayTime = "00:00";

        @JsonProperty("night")
        @Bindable
        public String nightTime = "00:00";

        @JsonProperty("time")
        @Bindable
        public String currentTime = "00:00";

        @JsonProperty("activity")
        @Bindable
        public Enums.DetectedActivityType detectedActivityType = Enums.DetectedActivityType.STILL;

        @JsonProperty("weather")
        @Bindable
        public Set<Enums.WeatherType> weatherTypes = new HashSet<>(Collections.emptyList());

        @SharedPreferencesAnnotation(path = "shared.preferences.timelapse",defaultValue = "false")
        @JsonProperty("timelapse")
        @Bindable
        public boolean isTimelapse = false;

        @JsonProperty("debugmode")
        @Bindable
        public boolean isDebugMode = false;
}
