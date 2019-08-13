package com.fangtang.tv.sdk.base.message;

public class Message {

    public int messageType;
    public Object messageData;
    public Object obj;

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", messageData=" + messageData +
                ", obj=" + obj +
                '}';
    }
}
