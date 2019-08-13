package com.fangtang.tv.sdk.download.core.listener;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.assist.FailReason;

/**
 * 初始化下载任务回调
 * Created by liulipeng on 15-9-9.
 */
public interface DownloadInfoInitCallback {

    void onSuccess(Download download);

    void onFailure(Download download, FailReason failReason);
}
