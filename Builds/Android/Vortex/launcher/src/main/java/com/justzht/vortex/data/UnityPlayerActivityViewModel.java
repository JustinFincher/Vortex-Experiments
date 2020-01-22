package com.justzht.vortex.data;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class UnityPlayerActivityViewModel extends BaseObservable
{
    @Bindable
    public boolean PermissionGranted = false;

    @Bindable
    public boolean WallpaperSet = false;

    @Bindable
    public float CardSlideProgress = 0;

    @Bindable
    public String AppVersionName;

    @Bindable
    public boolean GMSAvailable = true;

    @Bindable
    public boolean GMSResolvable = true;
}
