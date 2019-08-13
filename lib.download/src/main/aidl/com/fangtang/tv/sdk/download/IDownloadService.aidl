package com.fangtang.tv.sdk.download;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.IDownloadListener;

interface IDownloadService {

    void registerListener(in IDownloadListener listener);
    void unregisterListener(in IDownloadListener listener);

    void download(in Download info);

    Download getDownload(String downloadId);

    void start(String downloadId);

    void startAll();

    void pause(String downloadId);

    void pauseAll();

    void delete(String downloadId);

    void deleteAll();

    List<Download> getDownloadList();
}
