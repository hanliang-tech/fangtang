package com.fangtang.tv.sdk.base.app;

import android.support.annotation.Keep;

@Keep
public class AppInfoBean {

    public String appId;
    public String name;
    public String packageName;
    public String versionCode;
    public String url;
    public String md5;
    public String icon;
    public String path;

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", icon='" + icon + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
