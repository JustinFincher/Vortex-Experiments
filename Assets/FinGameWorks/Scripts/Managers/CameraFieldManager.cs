using System;
using UnityEngine;

namespace FinGameWorks.Scripts.Managers
{
    public class CameraFieldManager : UniSingleton<CameraFieldManager>
    {
        public Camera MainCam;
        public float MainCamSize = 4;
        private float currentAspect = 0;

        private void Start()
        {
            Application.targetFrameRate = 30;
        }

        private void Update()
        {
            if (Math.Abs(currentAspect - MainCam.aspect) > 0.01f)
            {
                currentAspect = MainCam.aspect;
                if (currentAspect > 1)
                {
                    MainCam.orthographicSize = MainCamSize / currentAspect;
                }
                else
                {
                    MainCam.orthographicSize = MainCamSize;
                }
            }
        }
        
        
        static string[] stringsFrom00To99 = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99"
        };
        
        private void OnGUI()
        {
            if (BridgeManager.Instance.CurrentAwarenessData != null && BridgeManager.Instance.CurrentAwarenessData.isDebugMode)
            {
                GUILayout.BeginArea (new Rect (10, 10, Screen.width-20, Screen.height-20));
                GUILayout.BeginVertical();
                GUILayout.FlexibleSpace();
                GUILayout.Label(stringsFrom00To99[Mathf.Clamp((int)(1f / Time.unscaledDeltaTime), 0, 99)] + " FPS ("+
                                (Time.unscaledDeltaTime * 1000f).ToString("#0.000") + " ms)");
                GUILayout.EndVertical();
                GUILayout.EndArea ();
            }
        }
    }
}