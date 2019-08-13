package com.fangtang.tv.sdk.leancloud;


public class LeanCloudMessageType {

    public static final int MESSAGE_TYPE_WECHAT_BIND_SUCCESS = 4;       // 微信绑定
    public static final int MESSAGE_TYPE_IOT_BIND_SUCCESS = 5;          // 音箱绑定
    public static final int MESSAGE_TYPE_IOT_UNBIND_SUCCESS = 51;       // 音箱解绑
    public static final int MESSAGE_TYPE_KEY_EVENT = 6;
    public static final int MESSAGE_TYPE_QUERY = 10;
    public static final int MESSAGE_TYPE_COMMAND = 100;
    public static final int MESSAGE_TYPE_UNKNOW = -1;
}
