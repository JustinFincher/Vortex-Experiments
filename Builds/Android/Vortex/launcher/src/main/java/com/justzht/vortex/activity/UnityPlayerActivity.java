package com.justzht.vortex.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.justzht.unity.lwp.LiveWallpaperManager;
import com.justzht.unity.lwp.LiveWallpaperPresentationActivity;
import com.justzht.vortex.BR;
import com.justzht.vortex.BuildConfig;
import com.justzht.vortex.R;
import com.justzht.vortex.data.UnityPlayerActivityViewModel;
import com.justzht.vortex.data.Values;
import com.justzht.vortex.databinding.LayoutUnityActivityBinding;
import com.justzht.vortex.helper.Utils;
import com.justzht.vortex.manager.AwarenessManager;
import com.tedpark.tedpermission.rx2.TedRx2Permission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.sentry.Sentry;

public class UnityPlayerActivity extends LiveWallpaperPresentationActivity
{
    public LayoutUnityActivityBinding dataBinding;
    private BottomSheetBehavior bottomSheetBehavior;
    public UnityPlayerActivityViewModel viewModel = new UnityPlayerActivityViewModel();

    @Override
    public void setInitialLayout() {
        dataBinding.unitySurfaceContainer.addView(surfaceView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.layout_unity_activity);
        dataBinding.setViewModel(viewModel);
        dataBinding.setActivity(this);
        dataBinding.setAwarenessData(AwarenessManager.INSTANCE.awarenessData);

        setSupportActionBar(dataBinding.bar);
        dataBinding.bar.setNavigationOnClickListener(v ->
        {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                dataBinding.settingsNestedScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        dataBinding.fab.setOnClickListener(view -> {
            LiveWallpaperManager.getInstance().openLiveWallpaperPreview();
        });

        bottomSheetBehavior = BottomSheetBehavior.from(dataBinding.bottomMenuExpandContainer);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                viewModel.CardSlideProgress = v;
                viewModel.notifyPropertyChanged(BR.CardSlideProgress);
            }
        });

        viewModel.AppVersionName = BuildConfig.VERSION_NAME;
        viewModel.notifyPropertyChanged(BR.AppVersionName);

        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(Values.ActionRequestPermissions))
        {
            requestPermissions();
        }
    }

    public void checkViewModelProperties()
    {
        boolean permissionsGranted = Utils.permissionsGranted();
        if (viewModel.PermissionGranted != permissionsGranted)
        {
            viewModel.PermissionGranted = permissionsGranted;
            viewModel.notifyPropertyChanged(BR.PermissionGranted);
        }

        boolean wallpaperSet = LiveWallpaperManager.getInstance().isWallpaperSet();
        if (viewModel.WallpaperSet != wallpaperSet)
        {
            viewModel.WallpaperSet = wallpaperSet;
            viewModel.notifyPropertyChanged(BR.WallpaperSet);
        }
    }

    public void requestPermissions()
    {
        checkViewModelProperties();
        if (!viewModel.PermissionGranted)
        {
            if (TedRx2Permission.canRequestPermission(this, Utils.getNecessaryPermissionsArray()))
            {
                Disposable disposable = TedRx2Permission.with(this)
                        .setRationaleTitle("Permission Request")
                        .setRationaleMessage("Location permission is needed to request weather and place data, while Physical Activity permission is needed to request motion type data.") // "we need permission for read contact and find your location"
                        .setPermissions(Utils.getNecessaryPermissionsArray())
                        .setDeniedMessage("Both permissions are needed to access data from Google Awareness API or this app won't work")
                        .request()
                        .subscribe(tedPermissionResult ->
                        {
                            checkViewModelProperties();
                            if (tedPermissionResult.isGranted())
                            {
                                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                                AwarenessManager.INSTANCE.start();
                            } else
                                {
                                Toast.makeText(this,
                                        "Permission Denied\n" + tedPermissionResult.getDeniedPermissions().toString(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }, Sentry::capture);
            }else
            {
                Toast.makeText(this, "Please Grant the Location Permission", Toast.LENGTH_SHORT).show();
                TedRx2Permission.startSettingActivityForResult(this);
            }
        }
    }

    public void resolveGMSAvailability()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        apiAvailability.getErrorDialog(this,
                Utils.getGMSResultCode(),
                getResources().getInteger(R.integer.ACTIVITY_REQUEST_CODE_GMS_RESOLVE))
                .show();
    }

    public void refreshDataManually()
    {
        AwarenessManager.INSTANCE.refresh();
    }

    public void exitAppCompletely()
    {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Exit app?")
                .setMessage("Vortex and its background service would exit completely until you re-open the app.")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    finishAffinity();
                    if (LiveWallpaperManager.getInstance().isWallpaperSet())
                    {
                        LiveWallpaperManager.getInstance().removePreviousWallpaper();
                    }
                    System.exit(0);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void checkTimeLapsePressed()
    {
        AwarenessManager.INSTANCE.awarenessData.isTimelapse = !AwarenessManager.INSTANCE.awarenessData.isTimelapse;
        AwarenessManager.INSTANCE.awarenessData.notifyPropertyChanged(BR.isTimelapse);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == getResources().getInteger(R.integer.ACTIVITY_REQUEST_CODE_GMS_RESOLVE))
        {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AwarenessManager.INSTANCE.start();
        dataBinding.bottomMenuExpandContainer.post(UnityPlayerActivity.this::checkResponsiveUI);

        viewModel.GMSAvailable = Utils.isGMSAvailable();
        viewModel.notifyPropertyChanged(BR.GMSAvailable);
        if (!viewModel.GMSAvailable)
        {
            viewModel.GMSResolvable = Utils.isGMSResolvable();
            viewModel.notifyPropertyChanged(BR.GMSResolvable);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkViewModelProperties();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction() != null && intent.getAction().equals(Values.ActionRequestPermissions))
        {
            requestPermissions();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.v("Vortex","onConfigurationChanged");

        dataBinding.bottomMenuExpandContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                dataBinding.bottomMenuExpandContainer.post(UnityPlayerActivity.this::checkResponsiveUI);
                dataBinding.bottomMenuExpandContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void checkResponsiveUI()
    {
        if (dataBinding.bottomMenuExpandContainer.getMeasuredWidth() * 0.5 > Utils.convertDpToPixel(600)
                &&
                dataBinding.bottomMenuExpandContainer.getMeasuredHeight() > Utils.convertDpToPixel(600))
        {
            dataBinding.settingsCardView.getLayoutParams().width = (int)Utils.convertDpToPixel(600);
        }else
        {
            dataBinding.settingsCardView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        dataBinding.settingsCardView.requestLayout();
    }
}