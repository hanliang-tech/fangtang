package com.fangtang.tv.sdk.base.push;

public class PushMessage {

    public int messageType;
    public Object messageData;
    public Object obj;

    @Override
    public String toString() {
        return "PushMessage{" +
                "messageType=" + messageType +
                ", messageData=" + messageData +
                ", obj=" + obj +
                '}';
    }
}
