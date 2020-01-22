using System;

namespace FinGameWorks.Scripts.Datas
{
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

        public static float NoiseFrequencyByWeatherType(WeatherType weatherType)
        {
            switch (weatherType)
            {
                case WeatherType.CLEAR:
                    return 0;
                case WeatherType.CLOUDY:
                    return 0.05f;
                case WeatherType.FOGGY:
                    return -0.05f;
                case WeatherType.HAZY:
                    return 0.02f;
                case WeatherType.ICY:
                    return -0.15f;
                case WeatherType.RAINY:
                    return 0.3f;
                case WeatherType.SNOWY:
                    return 0.1f;
                case WeatherType.STORMY:
                    return 0.4f;
                case WeatherType.UNKNOWN:
                    return 0;
                case WeatherType.WINDY:
                    return 0.2f;
                default:
                    return 0;
            }
        }
        
        public static float SimulationSpeedAdditionByWeatherType(WeatherType weatherType)
        {
            switch (weatherType)
            {
                case WeatherType.CLEAR:
                    return 0;
                case WeatherType.CLOUDY:
                    return 0;
                case WeatherType.FOGGY:
                    return -0.10f;
                case WeatherType.HAZY:
                    return -0.05f;
                case WeatherType.ICY:
                    return -0.1f;
                case WeatherType.RAINY:
                    return 0.15f;
                case WeatherType.SNOWY:
                    return 0.05f;
                case WeatherType.STORMY:
                    return 0.2f;
                case WeatherType.UNKNOWN:
                    return 0;
                case WeatherType.WINDY:
                    return 0.15f;
                default:
                    return 0;
            }
        }
    }
}