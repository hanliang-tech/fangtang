package com.fangtang.tv.sdk.base.device;

import android.content.Context;

import com.blankj.utilcode.util.AppUtils;

public class DeviceManager {

    private String appId;
    private String appKey;
    private int appVersionCode;
    private String channel;
    private String uuid;
    private Context context;
    private String packageName;

    private volatile static DeviceManager instance;

    public static DeviceManager getInstance() {
        if (instance == null) {
            synchronized (DeviceManager.class) {
                if (instance == null) {
                    instance = new DeviceManager();
                }
            }
        }
        return instance;
    }

    public synchronized void init(DeviceConfiguration configuration) {
        context = configuration.context;
        appId = configuration.appId;
        appKey = configuration.appKey;
        channel = configuration.channel;
        uuid = configuration.UUIDManager.generateUUID();
        appVersionCode = AppUtils.getAppVersionCode();
        packageName = context.getPackageName();
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey(){ return appKey;}

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public String getChannel() {
        return channel;
    }

    public String getUUID() {
        return uuid;
    }

    public Context getContext() {
        return context;
    }

    public String getPackageName() {
        return packageName;
    }
}
