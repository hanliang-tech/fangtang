package com.fangtang.tv.sdk.ui.utils;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;


public class VoiceStatus implements Serializable {

    private static final long serialVersionUID = 103L;

    public static String query;
    public static String domain;
    public static String queryId;
    public static String display;
    public static String tts;
    public static String intention;

    public static int defaultFocusIndex = -1;

    public static boolean isBind = false; // 是否绑定了小程序

    public static String appState = ""; // 状态
    public static String appTargetPck = ""; // 跳转包名

    @Nullable
    public static String downloadTipImg;

    public static String toStrings() {
        StringBuilder sb = new StringBuilder("{");
        try {
            for (Field f : VoiceStatus.class.getFields()) {
                sb.append(" ").append(f.getName()).append(":").append(f.get(null)).append(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("}");
        return sb.toString();
    }

    public static void saveInstance(@NotNull Bundle outState) {
        outState.putString("query", query);
        outState.putString("domain", domain);
        outState.putString("queryId", queryId);
        outState.putString("display", display);
        outState.putString("tts", tts);
        outState.putString("intention", intention);
        outState.putString("tipCover", downloadTipImg);
        outState.putBoolean("isBind", isBind);
    }

    public static void restoreSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
            domain = savedInstanceState.getString("domain");
            queryId = savedInstanceState.getString("queryId");
            display = savedInstanceState.getString("display");
            tts = savedInstanceState.getString("tts");
            intention = savedInstanceState.getString("intention");
            downloadTipImg = savedInstanceState.getString("tipCover");
            isBind = savedInstanceState.getBoolean("isBind", false);
        }
    }
}
