package com.fangtang.tv.sdk.base.bean;

import android.support.annotation.Keep;

@Keep
public class TensorFlowConfig {

    public String channelCode;
    public String version;
    public String url;

    @Override
    public String toString() {
        return "TensorFlowConfig{" +
                "channelCode='" + channelCode + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
