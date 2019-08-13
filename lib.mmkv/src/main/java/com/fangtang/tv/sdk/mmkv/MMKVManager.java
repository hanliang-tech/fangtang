package com.fangtang.tv.sdk.mmkv;

import android.app.Application;

import com.fangtang.tv.sdk.base.kv.IKVManager;
import com.tencent.mmkv.MMKV;

public class MMKVManager implements IKVManager {

    private volatile static MMKVManager instance;

    private MMKV mmkv;

    public static MMKVManager getInstance(Application application) {
        if (instance == null) {
            synchronized (MMKVManager.class) {
                if (instance == null) {
                    instance = new MMKVManager(application);
                }
            }
        }
        return instance;
    }

    private MMKVManager(Application application) {
        MMKV.initialize(application);
        mmkv = MMKV.defaultMMKV();
    }


    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.getBoolean(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return mmkv.getFloat(key, defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return mmkv.getInt(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return mmkv.getLong(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return mmkv.getString(key, defaultValue);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        mmkv.putBoolean(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        mmkv.putFloat(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        mmkv.putInt(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        mmkv.putLong(key, value);
    }

    @Override
    public void putString(String key, String value) {
        mmkv.putString(key, value);
    }
}


