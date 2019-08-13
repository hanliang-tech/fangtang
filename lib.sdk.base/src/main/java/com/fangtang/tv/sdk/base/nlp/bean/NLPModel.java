package com.fangtang.tv.sdk.base.nlp.bean;

public class NLPModel {

    public String channelCode;
    public String version;
    public String url;

    @Override
    public String toString() {
        return "NLPModel{" +
                "channelCode='" + channelCode + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
