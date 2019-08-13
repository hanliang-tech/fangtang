package com.fangtang.tv.sdk.download.core.listener;


import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.assist.FailReason;

/**
 * 初始化下载任务回调
 */
public interface DownloadDBInitCallback {

    void onSuccess(Download download);

    void onFailure(Download download, FailReason failReason);
}
