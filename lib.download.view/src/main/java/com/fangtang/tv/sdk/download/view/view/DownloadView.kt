package com.fangtang.tv.sdk.download.view.view

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ToastUtils
import com.fangtang.tv.sdk.base.logging.FLog
import com.fangtang.tv.sdk.download.view.contract.IDownloadContract
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel


class DownloadView(var context: Context) : IDownloadContract.IView {

    private val mWm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mWindowParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
    private var mIsShowing = false
    private var downloadContentView: DownloadContentView

    init {
        // 设置悬浮窗口长宽数据
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        // 设置window type
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST
        // 调整悬浮窗显示的停靠位置为左侧置顶
        // 设置图片格式，效果为背景透明
        mWindowParams.format = PixelFormat.RGBA_8888
        // 设置浮动窗口可以交互 && 硬件加速
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        mWindowParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        downloadContentView = DownloadContentView(context, null, 0)
    }

    override fun isDownloadViewShowing(): Boolean {
        return mIsShowing
    }


    override fun addDownload(downloadViewModel: DownloadViewModel) {
        downloadContentView.add(downloadViewModel)
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v("DownloadView", "------addDownload---${Thread.currentThread().name}------>>>>")
        }
        ToastUtils.showShort("为您安装 ${downloadViewModel.name} 请稍候")
        showDownloadView()
    }

    override fun removeDownload(downloadViewModel: DownloadViewModel) {
        downloadContentView.remove(downloadViewModel)
    }

    override fun updateDownload(downloadViewModel: DownloadViewModel) {
        downloadContentView.update(downloadViewModel)
    }

    override fun downloadSuccess(downloadViewModel: DownloadViewModel) {
        downloadContentView.downloadSuccess(downloadViewModel)
    }

    override fun installSuccess(downloadViewModel: DownloadViewModel) {
        downloadContentView.installSuccess(downloadViewModel)
    }

    override fun installError(downloadViewModel: DownloadViewModel) {
        downloadContentView.installError(downloadViewModel)
    }

    override fun installApp(downloadViewModel: DownloadViewModel) {
        downloadContentView.installApp(downloadViewModel)
    }

    override fun showDownloadView() {
        if (mIsShowing) {
            return
        }
        mIsShowing = true
        try {
            mWm.addView(downloadContentView, mWindowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun hiddenDownloadView() {
        try {
            mWm.removeViewImmediate(downloadContentView)
        } catch (e: Exception) {
//            e.printStackTrace()
        }
        mIsShowing = false
    }
}
