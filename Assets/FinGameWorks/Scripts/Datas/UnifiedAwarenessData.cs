using System;
using System.Collections.Generic;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using UnityEngine;
using Object = System.Object;

namespace FinGameWorks.Scripts.Datas
{
    [Serializable]
    public class UnifiedAwarenessData : Object
    {
        [SerializeField]
        [JsonProperty("temp")]
        public float FeelsLikeTemperatureInCelsius = 0;
        [SerializeField]
        [JsonProperty("hum")]
        public float Humidity = 0;
        [SerializeField]
        [JsonProperty("lon")]
        public double Longitude = 0;
        [SerializeField]
        [JsonProperty("lat")]
        public double Latitude = 0;
        [SerializeField]
        [JsonProperty("progress")]
        public float DayNightProgress = 0; // -1 to 1, -1 is sunset, 0 is sunrise, 1 is sunset
        [SerializeField]
        [JsonProperty("day")]
        public String DayTime = "0:00";
        [SerializeField]
        [JsonProperty("night")]
        public String NightTime = "0:00";
        [SerializeField]
        [JsonProperty("time")]
        public String CurrentTime = "0:00";
        [SerializeField]
        [JsonProperty("activity")]
        [JsonConverter(typeof(StringEnumConverter))]
        public Enums.DetectedActivityType DetectedActivityType = Enums.DetectedActivityType.STILL;
        [SerializeField]
        [JsonProperty("weather")]
        public List<Enums.WeatherType> WeatherTypes = new List<Enums.WeatherType>();
        [SerializeField]
        [JsonProperty("timelapse")]
        public bool isTimelapse = false;
        [SerializeField]
        [JsonProperty("debugmode")]
        public bool isDebugMode = false;
    }
}