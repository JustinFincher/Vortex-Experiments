using UnityEngine;
using Debug = System.Diagnostics.Debug;
#if UNITY_EDITOR
using System.IO;
using UnityEditor;
#endif
namespace FinGameWorks.Scripts.Editor
{
    [CreateAssetMenuAttribute(menuName = "FinGameWorks/EditorDataObject",fileName = "EditorData")]
    public class EditorDataObject : ScriptableObject
    {
        private static readonly string DefaultObjectPath = "Assets/FinGameWorks/Settings/EditorData.asset";
        public string buildTempPathRelativeToProj = "Builds/Android/Temp";
        public string buildOutPathRelativeToProj = "Builds/Android/Vortex";
        public string buildMainScenePathRelativeToProj = "Assets/FinGameWorks/Scenes/Field.unity";

#if UNITY_EDITOR
        public static EditorDataObject GetInstanceInEditor()
        {
            if (!File.Exists(GetAbsolutePathFromProjectRelative(DefaultObjectPath)))
            {
                EditorDataObject asset = ScriptableObject.CreateInstance<EditorDataObject>();
                AssetDatabase.CreateAsset(asset, DefaultObjectPath);
                AssetDatabase.SaveAssets();
            }
            return AssetDatabase.LoadAssetAtPath<EditorDataObject>(EditorDataObject.DefaultObjectPath);
        }

        public static string GetAbsolutePathFromProjectRelative(string path)
        {
            DirectoryInfo assetInfo = new DirectoryInfo(Application.dataPath);
            Debug.Assert(assetInfo.Parent != null, "assetInfo.Parent != null");
            return Path.Combine(assetInfo.Parent.FullName, path);
        }
#endif
    }
}