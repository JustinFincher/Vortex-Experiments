
using System;
using System.IO;
using UnityEditor;
using UnityEditor.Callbacks;
using UnityEngine;

namespace FinGameWorks.Scripts.Editor
{
    public class LWPPostBuildUtilities : EditorWindow
    {
        [MenuItem("Tools/FinGameWorks/Editor/LWP Build")]
        public static void Build()
        {
            BuildPlayerOptions buildPlayerOptions = new BuildPlayerOptions();
            buildPlayerOptions.scenes = new[] { EditorDataObject.GetInstanceInEditor().buildMainScenePathRelativeToProj };
            buildPlayerOptions.locationPathName =
                EditorDataObject.GetAbsolutePathFromProjectRelative(EditorDataObject.GetInstanceInEditor()
                    .buildTempPathRelativeToProj);
            buildPlayerOptions.target = BuildTarget.Android;
            buildPlayerOptions.targetGroup = BuildTargetGroup.Android;
            buildPlayerOptions.options = BuildOptions.None;
            BuildPipeline.BuildPlayer(buildPlayerOptions);
        }
        
        public static void CopyFromTempToMain()
        {
            DirectoryInfo tempUnityLibraryInfo = new DirectoryInfo(Path.Combine(EditorDataObject.GetAbsolutePathFromProjectRelative(EditorDataObject.GetInstanceInEditor().buildTempPathRelativeToProj),"unityLibrary"));
            DirectoryInfo mainUnityLibraryInfo = new DirectoryInfo(Path.Combine(EditorDataObject.GetAbsolutePathFromProjectRelative(EditorDataObject.GetInstanceInEditor().buildOutPathRelativeToProj),"unityLibrary"));
            FileUtil.DeleteFileOrDirectory(mainUnityLibraryInfo.FullName);
            CopyAll(tempUnityLibraryInfo,mainUnityLibraryInfo);
        }

        public static void ModifyGradleFile()
        {
            string gradlePath =
                Path.Combine(
                    EditorDataObject.GetAbsolutePathFromProjectRelative(EditorDataObject.GetInstanceInEditor()
                        .buildOutPathRelativeToProj), "unityLibrary", "build.gradle");
            
            File.WriteAllText(gradlePath, File.ReadAllText(gradlePath).Replace("implementation(name","api(name"));
        }

        public static void CopyAll(DirectoryInfo source, DirectoryInfo target)
        {
            Directory.CreateDirectory(target.FullName);

            // Copy each file into the new directory.
            foreach (FileInfo fi in source.GetFiles())
            {
                Console.WriteLine(@"Copying {0}\{1}", target.FullName, fi.Name);
                fi.CopyTo(Path.Combine(target.FullName, fi.Name), true);
            }

            // Copy each subdirectory using recursion.
            foreach (DirectoryInfo diSourceSubDir in source.GetDirectories())
            {
                DirectoryInfo nextTargetSubDir =
                    target.CreateSubdirectory(diSourceSubDir.Name);
                CopyAll(diSourceSubDir, nextTargetSubDir);
            }
        }
        
        [PostProcessBuild(Int32.MaxValue)]
        public static void OnPostprocessBuild(BuildTarget target, string pathToBuiltProject) {
            Debug.Log( pathToBuiltProject );
            if (pathToBuiltProject.Equals(EditorDataObject.GetAbsolutePathFromProjectRelative(EditorDataObject.GetInstanceInEditor().buildTempPathRelativeToProj)))
            {
                CopyFromTempToMain();
                ModifyGradleFile();
            }
        }
    }
}