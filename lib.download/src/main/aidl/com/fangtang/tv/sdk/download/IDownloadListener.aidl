package com.fangtang.tv.sdk.download;

import com.fangtang.tv.sdk.download.Download;

interface IDownloadListener {

     void onDownloadStatusChanged(out Download download);

     void onProgressUpdate(out Download download);

     void onDownloadListChanged();
}
