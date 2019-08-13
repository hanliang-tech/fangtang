package com.fangtang.tv.sdk.download.core;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.IDownloadListener;

import java.util.List;


public interface IDownloadManager {

    void registerListener(IDownloadListener listener);

    void unregisterListener(IDownloadListener listener);

    void download(Download info);

    void start(String downloadId);

    void startAll();

    void pause(String downloadId);

    void pauseAll();

    void delete(String downloadId);

    void deleteAll();

    List<Download> getDownloadList();

    Download getDownload(String downloadId);
}