package com.fangtang.tv.sdk.ui.tips.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleExecutor {

    private static final SingleExecutor ourInstance = new SingleExecutor();

    public static SingleExecutor getInstance() {
        return ourInstance;
    }


    private ExecutorService executorService;

    private SingleExecutor() {
        if(executorService == null){
            executorService = Executors.newSingleThreadExecutor();
//            executorService = Executors.newCachedThreadPool();
        }
    }

    public void execute(Runnable runnable){
        executorService.execute(runnable);
    }

}
