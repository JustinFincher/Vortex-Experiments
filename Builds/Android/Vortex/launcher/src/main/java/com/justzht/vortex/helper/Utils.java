package com.justzht.vortex.helper;

import android.Manifest;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.justzht.vortex.VortexApplication;
import com.tedpark.tedpermission.rx2.TedRx2Permission;

import java.util.ArrayList;

public class Utils
{
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int statusbarHeightPixel(Activity activity)
    {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        return statusBarHeight;
    }

    public static boolean isGMSAvailable()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(VortexApplication.getContext());
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static boolean isGMSResolvable()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(VortexApplication.getContext());
        return apiAvailability.isUserResolvableError(resultCode);
    }

    public static int getGMSResultCode()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(VortexApplication.getContext());
    }

    public static ArrayList<String> getNecessaryPermissions()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            list.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        return list;
    }

    public static String[] getNecessaryPermissionsArray()
    {
        return getNecessaryPermissions().toArray(new String[getNecessaryPermissions().size()]);
    }

    public static boolean permissionsGranted()
    {
        return TedRx2Permission.isGranted(VortexApplication.getContext(), getNecessaryPermissions().toArray(new String[getNecessaryPermissions().size()]));
    }
}
