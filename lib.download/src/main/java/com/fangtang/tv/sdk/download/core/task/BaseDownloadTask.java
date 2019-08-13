package com.fangtang.tv.sdk.download.core.task;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseDownloadTask implements Runnable {

    protected final AtomicBoolean isPaused = new AtomicBoolean();

    public boolean isPaused() {
        boolean paused = isPaused.get();
        return paused;
    }

    public boolean pause() {
        isPaused.set(true);
        return isPaused();
    }
}
