package com.fangtang.tv.sdk.base.remote.bean;

import android.support.annotation.Keep;

@Keep
public class RemoteDevice {

    public int id;
    public String device_id;
    public String source;
    public String name;

    @Override
    public String toString() {
        return "RemoteDevice{" +
                "id=" + id +
                ", device_id='" + device_id + '\'' +
                ", source='" + source + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
