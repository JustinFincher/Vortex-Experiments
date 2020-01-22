using System;
using DG.Tweening;
using FinGameWorks.Scripts.Datas;
using Newtonsoft.Json;
// using Sirenix.OdinInspector;
using UnityEngine;

namespace FinGameWorks.Scripts.Managers
{
    public class BridgeManager : UniSingleton<BridgeManager>
    {
        public ParticleSystem TargetParticle;
        
        [SerializeField]
        public AnimationCurve SimulationSpeedByDayNightProgressCurve;
        
        [SerializeField]
        public AnimationCurve ColorBySpeedRangeMaxByDayNightProgressCurve;
        
        [SerializeField]
        public AnimationCurve NoiseScrollSpeedByDayNightProgressCurve;
        
        [SerializeField]
        public AnimationCurve MaxParticlesByDayNightProgressCurve;

        [SerializeField]
        public UnifiedAwarenessData CurrentAwarenessData;

        [SerializeField] public Gradient StartColorOneByDayNightProgressCurve;
        [SerializeField] public Gradient StartColorTwoByDayNightProgressCurve;
        [SerializeField] public Gradient StartColorThreeByDayNightProgressCurve;
        [SerializeField] public Gradient StartColorFourByDayNightProgressCurve;
        [SerializeField] public Gradient ColorBySpeedByDayNightProgressCurve;

        public void ReceiveAwarenessData(String json)
        {
            try
            {
                UnifiedAwarenessData nextUnifiedAwarenessData = JsonConvert.DeserializeObject<UnifiedAwarenessData>(json);
                if (nextUnifiedAwarenessData != null)
                {
                    Debug.Log(nextUnifiedAwarenessData);
                    CurrentAwarenessData = nextUnifiedAwarenessData;
                    RefreshTargetValues();
                }
                else
                {
                    Debug.LogError("NextUnifiedAwarenessData == null");
                }
            }
            catch (Exception e)
            {
                Debug.LogException(e);
                Debug.LogError(e.StackTrace);
            }
        }

        // [Button]
        public void RefreshTargetValues()
        {
            TargetParticleNoiseFrequency = CurrentAwarenessData.Humidity / 100.0f * 0.03f + 0.08f;
            CurrentAwarenessData.WeatherTypes.ForEach(weather => TargetParticleNoiseFrequency += Enums.NoiseFrequencyByWeatherType(weather));

            TargetParticleNoiseStrengthConstant = 0.5f + 0.4f *
                                                  (CurrentAwarenessData.FeelsLikeTemperatureInCelsius + 40.0f) /
                                                  (60.0f + 40.0f)
                                                  - 0.2f * CurrentAwarenessData.Humidity / 100.0f;

            TargetSimulationSpeed = SimulationSpeedByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress) * (CurrentAwarenessData.isTimelapse ? 12 : 1);
            CurrentAwarenessData.WeatherTypes.ForEach(weather => TargetSimulationSpeed += Enums.SimulationSpeedAdditionByWeatherType(weather));

            TargetStartColor.mode = ParticleSystemGradientMode.RandomColor;
            TargetStartColor.gradient = new Gradient
            {
                colorKeys = new GradientColorKey[]
                {
                    new GradientColorKey(StartColorOneByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress / 2.0f + 0.5f), 0.0f), 
                    new GradientColorKey(StartColorTwoByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress / 2.0f + 0.5f), 0.5f), 
                    new GradientColorKey(StartColorThreeByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress / 2.0f + 0.5f), 0.7f), 
                    new GradientColorKey(StartColorFourByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress / 2.0f + 0.5f), 1.0f)
                }
            };
            
            TargetColorBySpeed.mode = ParticleSystemGradientMode.RandomColor;
            TargetColorBySpeed.gradient = new Gradient
            {
                colorKeys = new GradientColorKey[]
                {
                    new GradientColorKey(Color.white, 0.0f), 
                    new GradientColorKey(ColorBySpeedByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress / 2.0f + 0.5f), 1.0f), 
                },
                alphaKeys = new GradientAlphaKey[]
                {
                    new GradientAlphaKey(0.0f,0.0f),
                    new GradientAlphaKey(1.0f,0.5f) 
                }
            };

            TargetColorBySpeedRangeMax = ColorBySpeedRangeMaxByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress);

            TargetNoiseScrollSpeed =
                NoiseScrollSpeedByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress);

            TargetMaxParticles = MaxParticlesByDayNightProgressCurve.Evaluate(CurrentAwarenessData.DayNightProgress);
            
            Debug.Log("RefreshTargetValues" + 
                      "\nTargetParticleNoiseFrequency = " + TargetParticleNoiseFrequency + 
                      "\nTargetColorBySpeedRangeMax = " + TargetColorBySpeedRangeMax + 
                      "\nTargetNoiseScrollSpeed = " + TargetNoiseScrollSpeed + 
                      "\nTargetParticleNoiseStrengthConstant = " + TargetParticleNoiseStrengthConstant + 
                      "\nTargetSimulationSpeed = " + TargetSimulationSpeed+
                      "\nisTimelapse = " + CurrentAwarenessData.isTimelapse);
            

        }

        public float TargetParticleNoiseFrequency = 0.1f;
        public float TargetParticleNoiseStrengthConstant = 0.6f;
        public float TargetSimulationSpeed = 1.0f;
        public float TargetColorBySpeedRangeMax = 1.0f;
        public float TargetNoiseScrollSpeed = 0.2f;
        public float TargetMaxParticles = 1000.0f;
        
        public ParticleSystem.MinMaxGradient TargetStartColor = new ParticleSystem.MinMaxGradient();
        public ParticleSystem.MinMaxGradient TargetColorBySpeed = new ParticleSystem.MinMaxGradient();
        
        private void Update()
        {
            if (CurrentAwarenessData != null)
            {
                ParticleSystem.MainModule targetParticleMain = TargetParticle.main;
                targetParticleMain.simulationSpeed = Mathf.Lerp(targetParticleMain.simulationSpeed, TargetSimulationSpeed, 0.001f);
                targetParticleMain.startColor = TargetStartColor;
                targetParticleMain.maxParticles = (int) Mathf.Lerp(targetParticleMain.maxParticles, TargetMaxParticles, 0.002f);

                ParticleSystem.ColorBySpeedModule targetParticleColorBySpeed = TargetParticle.colorBySpeed;
                targetParticleColorBySpeed.color = TargetColorBySpeed;
                
                ParticleSystem.NoiseModule particleNoise = TargetParticle.noise;
                particleNoise.frequency = Mathf.Lerp(particleNoise.frequency, TargetParticleNoiseFrequency, 0.9f);
                particleNoise.strength = Mathf.Lerp(particleNoise.strength.constant,TargetParticleNoiseStrengthConstant,0.002f);
                particleNoise.scrollSpeedMultiplier = Mathf.Lerp(particleNoise.scrollSpeedMultiplier, TargetNoiseScrollSpeed, 0.001f);

                ParticleSystem.ColorBySpeedModule colorBySpeedModule = TargetParticle.colorBySpeed;
                colorBySpeedModule.range = new Vector2(colorBySpeedModule.range.x,
                    Mathf.Lerp(colorBySpeedModule.range.y,TargetColorBySpeedRangeMax,0.002f));
            }
        }

#if UNITY_EDITOR

        [TextArea]
        public String EditorTestJson;
        
        // [Button]
        public void LoadTestJson()
        {
            ReceiveAwarenessData(EditorTestJson);
        }
        // [Button]
        public void PrintCurrentJson()
        {
            String str = JsonConvert.SerializeObject(CurrentAwarenessData);
            Debug.Log(str);
        }
#endif
    }
}