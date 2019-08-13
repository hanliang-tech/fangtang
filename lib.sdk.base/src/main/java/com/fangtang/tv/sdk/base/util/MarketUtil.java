package com.fangtang.tv.sdk.base.util;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MarketUtil {

    //    private static final Map<String, String> MARKETS = new HashMap<>(2);
    private static final String PCK_FT = "com.fangtang.tv";
    private static final String PCK_FREE_WATCH = "tv.huan.freewatch";

    private static String flavor;

    private static String pckName;

//    static {
//        MARKETS.put("com.tcl.appmarket2", "tcl");
//        MARKETS.put("com.changhong.appstore", "changhong");
//    }

    private volatile static String currentMarket = "";
    private volatile static String currentChannel = "";

//    public static String getMarket() {
//        if (TextUtils.isEmpty(currentMarket)) {
//            try {
//                for (String market : MARKETS.keySet()) {
//                    if (isAppInstalled(market)) {
//                        currentMarket = market;
//                        break;
//                    }
//                }
//            } catch (Exception e) {
////                e.printStackTrace();
//            }
//        }
//        return currentMarket;
//    }

    public static boolean isAppInstalled(@NonNull final String packageName) {
        PackageManager packageManager = Utils.getApp().getPackageManager();
        try {
            return packageManager.getApplicationInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getChannel() {
        if (TextUtils.isEmpty(currentChannel)) {
            currentChannel = getFlavor();
            if (currentChannel.equals("huan") || currentChannel.equals("devh")) {
                currentChannel = "tcl";
            }
        }
        return currentChannel;
    }

    /**
     * 本应用是方糖？
     *
     * @return
     */
    public static boolean isFt() {
        return PCK_FT.equals(getPackageName());
    }

    /**
     * 本应用是自由看？
     *
     * @return
     */
    public static boolean isFreeWatch() {
        return PCK_FREE_WATCH.equals(getPackageName());
    }

    /**
     * 是否TCL电视
     *
     * @return
     */
    public static boolean isTCL() {
        return "tcl".equals(getChannel());
//        return true;
    }

    public static boolean isXiaomi() {
        return "xiaomi".equals(getFlavor());
    }

    @NotNull
    public static String getFlavor() {
        return flavor;
    }

    public static void updateFlavor(@Nullable String channel) {
        flavor = channel;
    }

    public static boolean canDownload() {
        try {
            return ReflectUtils.reflect(getPackageName() + ".BuildConfig")
                    .field("DOWNLOADABLE")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShiJiu() {
        return "shijiu".equals(getFlavor());
    }

    public static String getPackageName(){
        if(!TextUtils.isEmpty(pckName)) return pckName;
        int tryCount = 0;
        do {
            try {
                tryCount++;
                pckName = AppUtils.getAppPackageName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (TextUtils.isEmpty(pckName) && tryCount < 5);
        return pckName;
    }
}
