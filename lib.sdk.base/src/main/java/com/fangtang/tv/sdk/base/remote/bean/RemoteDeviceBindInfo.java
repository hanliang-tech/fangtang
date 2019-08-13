package com.fangtang.tv.sdk.base.remote.bean;

import android.support.annotation.Keep;
import android.util.Log;

import com.fangtang.tv.sdk.base.debug.DebugStatus;
import com.google.gson.JsonElement;

import java.util.List;

@Keep
public class RemoteDeviceBindInfo {

    public List<RemoteDevice> devices;
    public List<JsonElement> wechat;
    public String tv_id;
    public String connected_image;
    public String un_connected_image;

    public boolean isBindAnyDevice(){
        return isBindIotDevice() || isBindWeChat();
    }

    public boolean isBindIotDevice(){
        boolean result = devices != null && devices.size() > 0;
        if(DebugStatus.INSTANCE.isDebug()) Log.d("RemoteDeviceBindInfo", "绑定音箱？" + result);
        return result;
    }

    public boolean isBindWeChat(){
        boolean result =  wechat != null && wechat.size() > 0;
        if(DebugStatus.INSTANCE.isDebug()) Log.d("RemoteDeviceBindInfo", "绑定微信？" + result);
        return result;
    }

    @Override
    public String toString() {
        return "RemoteDeviceBindInfo{" +
                "devices=" + devices +
                ", tv_id='" + tv_id + '\'' +
                ", connected_image='" + connected_image + '\'' +
                ", un_connected_image='" + un_connected_image + '\'' +
                '}';
    }
}
