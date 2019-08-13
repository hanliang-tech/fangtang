package com.fangtang.tv.sdk.base.push;

public interface IPushManager {

    void notifyPushMessage(PushMessage pushMessage);

    void release();

    interface PushCallback {
        void onSuccess();

        void onError(Exception e);
    }

    void open(PushCallback callback);

    void close(PushCallback callback);


    interface ReceivePushMessageListener {
        void onReceivePushMessage(PushMessage pushMessage);
    }

    boolean addReceivePushMessageListener(ReceivePushMessageListener listener);

    boolean removeReceivePushMessageListener(ReceivePushMessageListener listener);
}
