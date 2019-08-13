package com.fangtang.tv.sdk.base.push;

import android.support.annotation.Keep;

@Keep
public class PushConfigBean {

    public String api;
    public String push;
    public String rtm;
    public String stats;
    public String engine;

    public String limitDay; // 多少天不使用LeanCloud服务 就不再主动启动

    @Override
    public String toString() {
        return "PushConfigBean{" +
                "api='" + api + '\'' +
                ", push='" + push + '\'' +
                ", rtm='" + rtm + '\'' +
                ", stats='" + stats + '\'' +
                ", engine='" + engine + '\'' +
                ", limitDay='" + limitDay + '\'' +
                '}';
    }
}
