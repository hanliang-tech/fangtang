package com.fangtang.tv.sdk.base.bean;

import org.jetbrains.annotations.Nullable;

public class VoiceCommand {

    public int index;
    @Nullable
    public String flag;
    public int functionType;
    public int sequence;
    @Nullable
    public String nlpJson;
    @Nullable
    public String saveTxt;

    @Override
    public String toString() {
        return "VoiceCommand{" +
                "index=" + index +
                ", flag='" + flag + '\'' +
                ", functionType=" + functionType +
                ", sequence=" + sequence +
                ", nlpJson='" + nlpJson + '\'' +
                ", saveTxt='" + saveTxt + '\'' +
                '}';
    }
}
