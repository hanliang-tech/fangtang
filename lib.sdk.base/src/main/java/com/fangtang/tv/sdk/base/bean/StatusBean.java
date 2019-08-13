package com.fangtang.tv.sdk.base.bean;

import android.support.annotation.Keep;

@Keep
public class StatusBean<T> {
    public int status = -1;
    public int errorCode;
    public String msg;
    public T data;

    public boolean isSuccess() {
        return errorCode == 0;
    }

    @Override
    public String toString() {
        return "StatusBean{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
