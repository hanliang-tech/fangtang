package com.fangtang.tv.sdk.base.remote.bean;

import android.support.annotation.Keep;

import com.google.gson.JsonElement;

import java.util.List;


@Keep
public class Remote {

    public Speaker speaker;
    public List<String> wechat;

    public boolean hasIotDevice(){
        int len = 0;
        try {
            len = speaker.devices.size();
        } catch (Exception e) {
        }
        return len > 0;
    }

    public boolean hasWechat(){
        int len = 0;
        try {
            len = wechat.size();
        } catch (Exception e) {
        }
        return len > 0;
    }

    @Override
    public String toString() {
        return "Remote{" +
                "speaker=" + speaker +
                ", wechat=" + wechat +
                '}';
    }

    @Keep
    public static class Speaker{
        public List<JsonElement> devices;

        @Override
        public String toString() {
            return "Speaker{" +
                    "devices=" + devices +
                    '}';
        }
    }

}
