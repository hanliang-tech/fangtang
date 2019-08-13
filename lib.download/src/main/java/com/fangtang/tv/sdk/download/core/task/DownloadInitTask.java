package com.fangtang.tv.sdk.download.core.task;

import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.listener.DownloadDBInitCallback;


public class DownloadInitTask implements Runnable {

    private DownloaderConfiguration configuration;
    private IDownloadDBHelper downloadDBHelper;
    private DownloadDBInitCallback initCallback;
    private Download download;

    public DownloadInitTask(DownloaderConfiguration configuration, Download download, DownloadDBInitCallback initCallback) {
        this.configuration = configuration;
        this.downloadDBHelper = configuration.downloadDBHelper;
        this.initCallback = initCallback;
        this.download = download;
    }

    @Override
    public void run() {
        try {
            //save to db
            download.setStatus(Constant.DOWNLOAD_STATE_INIT);
            downloadDBHelper.saveDownload(download);
            if (initCallback != null) {
                initCallback.onSuccess(download);
            }
        } catch (Exception e) {
            e.printStackTrace();
            download.setStatus(Constant.DOWNLOAD_STATE_INIT_ERROR);
            downloadDBHelper.saveDownload(download);
            if (initCallback != null) {
                initCallback.onFailure(download, null);
            }
        }
    }
}
