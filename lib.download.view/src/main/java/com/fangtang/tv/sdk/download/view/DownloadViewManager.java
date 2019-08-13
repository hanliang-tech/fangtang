package com.fangtang.tv.sdk.download.view;


import android.content.Context;

import com.fangtang.tv.sdk.download.view.model.DownloadViewModel;
import com.fangtang.tv.sdk.download.view.view.DownloadView;

public class DownloadViewManager {

    private static DownloadViewManager instance;
    private DownloadView downloadView;
    private DownloadPresenter downloadPresenter;

    private DownloadViewManager() {
    }

    public synchronized static DownloadViewManager getInstance() {
        if (instance == null) {
            synchronized (DownloadViewManager.class) {
                if (instance == null) {
                    instance = new DownloadViewManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        downloadView = new DownloadView(context);
        downloadPresenter = new DownloadPresenter(context, downloadView);
    }

    public void addDownload(DownloadViewModel downloadViewModel) {
        downloadPresenter.addDownload(downloadViewModel);
    }

    public void downloadSuccess(DownloadViewModel downloadViewModel) {
        downloadPresenter.downloadSuccess(downloadViewModel);
    }

    public void downloadError(DownloadViewModel downloadViewModel) {
        downloadPresenter.downloadError(downloadViewModel);
    }

    public void installSuccess(DownloadViewModel downloadViewModel) {
        downloadPresenter.installSuccess(downloadViewModel);
    }

    public void installApp(DownloadViewModel downloadViewModel) {
        downloadPresenter.installApp(downloadViewModel);
    }

    public void installError(DownloadViewModel downloadViewModel) {
        downloadPresenter.installError(downloadViewModel);
    }

    public void updateDownloadStatus(DownloadViewModel downloadViewModel) {
        downloadPresenter.updateDownloadStatus(downloadViewModel);
    }
}
