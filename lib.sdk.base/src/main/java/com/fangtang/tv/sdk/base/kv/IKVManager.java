package com.fangtang.tv.sdk.base.kv;

public interface IKVManager {

    //put
    void putString(String key, String value);

    void putInt(String key, int value);

    void putFloat(String key, float value);

    void putLong(String key, long value);

    void putBoolean(String key, boolean value);


    //get
    String getString(String key, String defaultValue);

    int getInt(String key, int defaultValue);

    float getFloat(String key, float defaultValue);

    long getLong(String key, long defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

}
