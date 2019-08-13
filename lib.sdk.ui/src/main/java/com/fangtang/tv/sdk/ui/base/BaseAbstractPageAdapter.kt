package com.fangtang.tv.sdk.ui.base

import com.fangtang.tv.waterfall.GridDataFactory
import com.fangtang.tv.waterfall.GridDataHelper
import com.fangtang.tv.waterfall.Waterfall
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.Constant.DEFAULT_PAGE_SIZE
import com.fangtang.tv.sdk.base.debug.DebugStatus
import com.fangtang.tv.sdk.base.logging.logE
import com.fangtang.tv.sdk.base.logging.logW
import com.fangtang.tv.sdk.ui.model.WaterfallModel
import com.fangtang.tv.sdk.ui.page.VoiceCustomDataFactory

abstract class BaseAbstractPageAdapter : GridDataHelper.OnLoadMoreCallback() {

    private var mGridDataFactory: GridDataFactory? = null
    protected var mGridDataHelper: GridDataHelper? = null
    private var mCacheData: List<WaterfallModel>? = null

    protected var mSpanCount = 0

    // 需要请求分页数据
    protected var mNeedLoadMore = true

    protected var pageSize = DEFAULT_PAGE_SIZE

    open fun setUp(waterfallPageView: Waterfall.IPageInterface, spanCount: Int, flag:String? = null) {
        mSpanCount = spanCount
        // 将GridDataHelper与瀑布流绑定
        // 这里自定义了一个DataGridFactory 目的是影响瀑布流的分组规则
        mGridDataHelper = GridDataHelper(VoiceCustomDataFactory(waterfallPageView, spanCount, getRowDistance(), flag))

        if (hasPageSplit()) {
            val dataLoadFactory = GridDataHelper.MoreDataFactory(pageSize, this)
            mGridDataHelper?.setLoadMoreDataFactory(dataLoadFactory)
            mGridDataHelper?.listenLoadMoreDataCallback()
        }
    }

    open fun startLoadData(dataList: List<WaterfallModel>?) {
        mCacheData = dataList
//        logE("startLoadData dataList:$dataList ,mNeedLoadMore:$mNeedLoadMore")
        if (mCacheData == null) {
            logE { "无数据！！！" }
            return
        }

        if (mNeedLoadMore && hasPageSplit()) {
            if (DebugStatus.isDebug()) {
                logW { "需要分页 gogogo" }
            }
            mGridDataHelper?.addGridDataDirectly(mCacheData, true)
        }
    }

    override fun loadMore(page: Int, pageSize: Int, helper: GridDataHelper?): Boolean {
        if (DebugStatus.isDebug()) {
            logW { "开始分页 page:$page, pageSize:$pageSize, needMore:$mNeedLoadMore" }
        }
        if (!mNeedLoadMore) {
            if (DebugStatus.isDebug()) {
                logW { "无需加载----" }
            }
            return false
        }
//        if (page == mFirstPage) {
//            if (DebugStatus.isDebug()) {
//                logW("第一页直接显示")
//            }
//            helper?.addGridDataDirectly(mCacheData, false)
//        }
        if (DebugStatus.isDebug()) {
            logW { "加载第 $page 页" }
        }
        loadNextPage(page, helper)
        return mNeedLoadMore
    }

    fun onDestroy() {
        mGridDataHelper?.disable()
        mGridDataHelper?.clearGridData()
        mGridDataHelper?.gridDataFactory?.clearRemainerList()
    }

    // 每行间距
    protected open fun getRowDistance(): Int {
        return Constant.DISTANCE_ITEM
    }

    open fun hasPageSplit(): Boolean {
        return false
    }

    // 加载数据
    abstract fun loadNextPage(pageNumber: Int, helper: GridDataHelper?)

}