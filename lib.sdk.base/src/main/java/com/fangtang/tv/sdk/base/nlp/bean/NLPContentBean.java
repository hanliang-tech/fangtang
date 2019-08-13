package com.fangtang.tv.sdk.base.nlp.bean;

import android.support.annotation.Keep;

import com.google.gson.JsonElement;

@Keep
public class NLPContentBean {

    public String display;
    public String tts;
    public JsonElement reply;

    @Override
    public String toString() {
        return "NLPContentBean{" +
                "display='" + display + '\'' +
                ", tts='" + tts + '\'' +
                ", reply=" + reply +
                '}';
    }
}
