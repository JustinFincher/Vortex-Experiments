![](PressKit/Banner.jpg)
# Vortex

![GitHub](https://img.shields.io/github/license/JustinFincher/Vortex-Experiments)
![GitHub repo size](https://img.shields.io/github/repo-size/JustinFincher/Vortex-Experiments)
![[Google Play](https://play.google.com/store/apps/details?id=com.justzht.vortex)](https://img.shields.io/badge/Google%20Play-Download-brightgreen)

## Introduction
Vortex, a Data-driven Live Wallpaper ([Google Play](https://play.google.com/store/apps/details?id=com.justzht.vortex)), is an Unity-Android hybrid live wallpaper developed by [JustZht](https://fincher.im/). 
Based on data acquired from [Awareness API](https://developers.google.com/awareness/overview) including weather, location, time, human motion, Vortex manipulate the flowing particles with different parameters.

## Project Structure
Vortex consists of two parts: Unity as the graphical frontends, and Android as the data provider.  

```
.
├── Assets
│   ├── Demigiant // free version of DOTween
│   ├── Editor
│   ├── FinGameWorks // my code module
│   ├── JsonDotNet // free version of Json.Net
│   ├── Plugins // my own LWP solution
│   └── Resources
├── Builds
│   └── Android
│       ├── Temp // temp export directory
│       │   ├── launcher
│       │   └── unityLibrary
│       └── Vortex // main android project
│           ├── launcher
│           └── unityLibrary
├── Packages
└── ProjectSettings
```

Unity (>=2019.3) would export the updated Android Gradle project to the `Builds/Android/Temp` directory, and a [post-processing script](Assets/FinGameWorks/Scripts/Editor/LWPPostBuildUtilities.cs) would copy the newly generated unityLibrary directory to the same place in `Builds/Android/Vortex`, essentially replacing the old one. The unityLibrary directory contains the often-updated Unity part, while the launcher directory contains the persistent, native Android part, including java files and xmls.

Vortex deploys my own solution for Unity-Android live wallpaper, called UniLWP, which is both [free for the basic version](https://github.com/JustinFincher/UniLWP-NoDeps) and commercially on sale (Asset Store WIP) for a complete feature set.

## Build Instructions

- Fill out [sentry.properties](Builds/Android/Vortex/sentry.properties)
- Fill out [google-services.json](Builds/Android/Vortex/launcher/google-services.json), the Google project needs to be Awareness API enabled.
- Open repo in Unity (>= 2019.3.0f1), then trigger a build by menu path: `FinGameWorks->Editor->Build`
- Open the Android project at [Builds/Android/Vortex/](Builds/Android/Vortex/)
- Build
