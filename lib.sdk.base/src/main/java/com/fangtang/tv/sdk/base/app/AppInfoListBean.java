package com.fangtang.tv.sdk.base.app;


import java.util.List;

public class AppInfoListBean {

    public String channelCode;
    public List<AppInfoBean> list;

    @Override
    public String toString() {
        return "AppInfoListBean{" +
                "channelCode='" + channelCode + '\'' +
                ", list=" + list +
                '}';
    }
}
