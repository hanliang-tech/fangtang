package com.fangtang.tv.sdk.download.view.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.fangtang.tv.sdk.base.logging.FLog
import com.fangtang.tv.sdk.download.view.R
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel

class DownloadContentView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
    : LinearLayout(context, attrs, defStyleAttr) {

    private var item1: DownloadTextView
    private var item2: DownloadTextView
    private var item3: DownloadTextView

    private var downloadItemList: HashMap<String, DownloadTextView> = HashMap()

    init {
        View.inflate(context, R.layout.view_downlaod, this)
        item1 = findViewById(R.id.tv_content)
        item2 = findViewById(R.id.tv_content2)
        item3 = findViewById(R.id.tv_content3)
    }

    fun add(downloadItem: DownloadViewModel) {
        if (downloadItemList.size >= 3) {
            return
        }
        val size = downloadItemList.size
        when (size) {
            0 -> {
                downloadItem.let {
                    if (FLog.isLoggable(FLog.VERBOSE)) {
                        FLog.v("DownloadContentView", "---add----downloadItem---$downloadItem------>>>>")
                    }
                    downloadItemList[downloadItem.id] = item1
                    item1.visibility = View.VISIBLE
                    update(downloadItem)
                }
            }

            1 -> {
                downloadItem.let {
                    downloadItemList[downloadItem.id] = item2
                    item2.visibility = View.VISIBLE
                    update(downloadItem)
                }
            }

            2 -> {
                downloadItem.let {
                    downloadItemList[downloadItem.id] = item3
                    item3.visibility = View.VISIBLE
                    update(downloadItem)
                }
            }
        }
    }

    fun remove(downloadViewModel: DownloadViewModel) {
        resetDownloadItem(downloadViewModel)
    }

    fun installApp(downloadViewModel: DownloadViewModel) {
        val item = downloadItemList[downloadViewModel.id]
        item?.text = ("安装${downloadViewModel.name}，请稍候...")
        remove(downloadViewModel)
    }

    fun update(downloadViewModel: DownloadViewModel) {
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v("DownloadContentView", "-------update----$downloadViewModel----->>>>")
        }
        val item = downloadItemList[downloadViewModel.id]
        item?.onUpdate(downloadViewModel.progress)

        if (downloadViewModel.progress < 1.0f) {
            item?.text = "下载${downloadViewModel.name}，请稍候..."
        } else if (downloadViewModel.progress == 1.0f) {
            item?.text = ("下载${downloadViewModel.name}完成，等待安装...")
        }
    }

    fun downloadSuccess(downloadViewModel: DownloadViewModel) {
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v("DownloadContentView", "-------downloadSuccess----$downloadViewModel----->>>>")
        }
        val item = downloadItemList[downloadViewModel.id]
        item?.onUpdate(downloadViewModel.progress)
        item?.text = ("下载${downloadViewModel.name}完成，等待安装...")
    }

    private fun resetDownloadItem(downloadViewModel: DownloadViewModel) {
        val item = downloadItemList[downloadViewModel.id]
        item?.visibility = View.GONE
        item?.visibility = View.GONE
        item?.onUpdate(0f)
        item?.text = ""
        downloadItemList.remove(downloadViewModel.id)
    }

    fun installSuccess(downloadViewModel: DownloadViewModel) {
        val item = downloadItemList[downloadViewModel.id]
        item?.text = ("安装${downloadViewModel.name}成功")
        remove(downloadViewModel)
    }

    fun installError(downloadViewModel: DownloadViewModel) {
        val item = downloadItemList[downloadViewModel.id]
        item?.text = ("安装${downloadViewModel.name}失败")
        remove(downloadViewModel)
    }
}