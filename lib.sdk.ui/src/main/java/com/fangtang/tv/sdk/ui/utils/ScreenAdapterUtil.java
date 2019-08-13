package com.fangtang.tv.sdk.ui.utils;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class ScreenAdapterUtil {


    // 系统的Density
    private static float sNoncompatDensity;
    // 系统的ScaledDensity
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(Context activity, Application application, float targetDensity) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = displayMetrics.density;
            sNoncompatScaledDensity = displayMetrics.scaledDensity;
            // 监听在系统设置中切换字体
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity=application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        // 此处以360dp的设计图作为例子
//        float targetDensity=displayMetrics.widthPixels/360;
        float targetScaledDensity=targetDensity*(sNoncompatScaledDensity/sNoncompatDensity);
        int targetDensityDpi= (int) (160 * targetDensity);
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }


    public static void setCustomDensityFixedWidth(Context activity, Application application, final float fixedDevWidth) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        float targetDensity=displayMetrics.widthPixels/fixedDevWidth;
        setCustomDensity(activity,application,targetDensity);
    }
}
