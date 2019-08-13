package com.fangtang.tv.sdk.download.core.listener;


import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.assist.FailReason;

import java.util.List;

/**
 * 下载初始化回调
 * Created by liulipeng on 15-9-9.
 */
public interface DownloadListInitCallback {

    void onSuccess(List<Download> downloadList);

    void onFailure(FailReason failReason);
}
