package com.fangtang.tv.sdk.download.view

import android.content.Context
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.fangtang.tv.sdk.download.view.contract.IDownloadContract
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel


class DownloadPresenter(private val context: Context, private val downloadView: IDownloadContract.IView?)
    : IDownloadContract.IPresenter {

    private val downloadViewModelList = mutableListOf<DownloadViewModel>()

    override fun addDownload(downloadViewModel: DownloadViewModel) {
        if (!downloadViewModelList.contains(downloadViewModel)) {
            downloadViewModelList.add(downloadViewModel)
            downloadView?.addDownload(downloadViewModel)
        } else {
            updateDownloadStatus(downloadViewModel)
        }
    }

    override fun downloadError(downloadViewModel: DownloadViewModel) {
        Toast.makeText(context, "下载${downloadViewModel.name}失败，请重试！", Toast.LENGTH_SHORT).show()
        downloadView?.removeDownload(downloadViewModel)
        downloadView?.hiddenDownloadView()
    }

    override fun downloadSuccess(downloadViewModel: DownloadViewModel) {
        downloadView?.downloadSuccess(downloadViewModel)
    }

    override fun updateDownloadStatus(downloadViewModel: DownloadViewModel) {
        downloadView?.updateDownload(downloadViewModel)
    }

    override fun installError(downloadViewModel: DownloadViewModel) {
        Toast.makeText(context, "安装${downloadViewModel.name}失败，请重试！", Toast.LENGTH_SHORT).show()
        downloadView?.installError(downloadViewModel)
        downloadView?.hiddenDownloadView()
    }

    override fun installSuccess(downloadViewModel: DownloadViewModel) {
        ToastUtils.showShort("安装完勿直接打开，返回并再次点击影片")
        downloadView?.installSuccess(downloadViewModel)
        downloadView?.hiddenDownloadView()
        //delete
        downloadViewModelList.remove(downloadViewModel)
    }

    override fun installApp(downloadViewModel: DownloadViewModel) {
        downloadView?.installApp(downloadViewModel)
    }
}