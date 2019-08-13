package com.fangtang.tv.sdk.base.message;


import android.app.Application;

import com.fangtang.tv.sdk.base.push.IPushManager;
import com.fangtang.tv.sdk.base.push.PushMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageManager implements IPushManager.ReceivePushMessageListener {

    private volatile static MessageManager instance;

    private List<ReceiveMessageListener> messageListenerList
            = Collections.synchronizedList(new ArrayList<ReceiveMessageListener>());

    private IPushManager pushManager;

    private MessageManager() {
    }

    public synchronized static MessageManager getInstance() {
        if (instance == null) {
            synchronized (MessageManager.class) {
                if (instance == null) {
                    instance = new MessageManager();
                }
            }
        }
        return instance;
    }

    public void init(Application application, IPushManager pushManager) {
        this.pushManager = pushManager;
        this.pushManager.addReceivePushMessageListener(this);
    }

    @Override
    public void onReceivePushMessage(PushMessage pushMessage) {

    }

    public void release(){
        this.pushManager.removeReceivePushMessageListener(this);
    }

    interface ReceiveMessageListener {
        void onReceivePushMessage(Message message);
    }

    public boolean addReceiveMessageListener(ReceiveMessageListener listener) {
        if (messageListenerList.contains(listener)) {
            messageListenerList.add(listener);
        }
        return true;
    }


    public boolean removeReceiveMessageListener(ReceiveMessageListener listener) {
        return messageListenerList.remove(listener);
    }
}
