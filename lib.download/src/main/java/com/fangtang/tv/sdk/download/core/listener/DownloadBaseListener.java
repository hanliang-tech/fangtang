package com.fangtang.tv.sdk.download.core.listener;


import com.fangtang.tv.sdk.download.Download;

public interface DownloadBaseListener {
    /**
     * 下载状态改变
     */
    void onDownloadStatusChanged(Download download);

    /**
     * 下载列表改变
     */
    void onDownloadListChanged();

    /**
     * 下载进度变化
     */
    void onProgressUpdate(Download download);
}
