package com.fangtang.tv.sdk.download.view.contract

import com.fangtang.tv.sdk.download.view.model.DownloadViewModel

interface IDownloadContract {

    interface IView {
        fun addDownload(downloadViewModel: DownloadViewModel)
        fun removeDownload(downloadViewModel: DownloadViewModel)
        fun updateDownload(downloadViewModel: DownloadViewModel)
        fun downloadSuccess(downloadViewModel: DownloadViewModel)
        fun installApp(downloadViewModel: DownloadViewModel)
        fun installSuccess(downloadViewModel: DownloadViewModel)
        fun installError(downloadViewModel: DownloadViewModel)
        fun showDownloadView()
        fun hiddenDownloadView()
        fun isDownloadViewShowing(): Boolean
    }

    interface IPresenter {

        fun addDownload(downloadViewModel: DownloadViewModel)

        fun downloadSuccess(downloadViewModel: DownloadViewModel)
        fun downloadError(downloadViewModel: DownloadViewModel)

        fun installSuccess(downloadViewModel: DownloadViewModel)
        fun installError(downloadViewModel: DownloadViewModel)
        fun installApp(downloadViewModel: DownloadViewModel)

        fun updateDownloadStatus(downloadViewModel: DownloadViewModel)
    }
}