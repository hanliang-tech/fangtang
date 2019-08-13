package com.fangtang.tv.sdk.download.core.task;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.listener.DownloadListInitCallback;

import java.util.List;

/**
 * 初始化下载任务
 * Created by liulipeng on 15-9-9.
 */
public class DownloadListInitTask implements Runnable {

    private DownloaderConfiguration configuration;
    private IDownloadDBHelper downloadDBHelper;
    private DownloadListInitCallback initCallback;

    public DownloadListInitTask(DownloaderConfiguration configuration, DownloadListInitCallback initCallback) {
        this.configuration = configuration;
        this.downloadDBHelper = configuration.downloadDBHelper;
        this.initCallback = initCallback;
    }

    @Override
    public void run() {
        try {
            List<Download> downloadList = downloadDBHelper.queryAllDownloadAndTask();
            if (initCallback != null) {
                initCallback.onSuccess(downloadList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (initCallback != null) {
                initCallback.onFailure(null);
            }
        }
    }
}
