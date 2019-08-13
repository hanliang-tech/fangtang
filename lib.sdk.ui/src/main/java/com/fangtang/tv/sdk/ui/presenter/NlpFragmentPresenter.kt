package com.fangtang.tv.sdk.ui.presenter

import android.content.Context
import android.graphics.Color
import android.support.v17.leanback.widget.Presenter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.Constant.ROW_COUNT
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.bean.VoiceCommand
import com.fangtang.tv.sdk.base.logging.logEF
import com.fangtang.tv.sdk.base.logging.logW
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.activity.NlpActivity
import com.fangtang.tv.sdk.ui.base.BaseAbstractPageAdapter
import com.fangtang.tv.sdk.ui.base.BaseContract
import com.fangtang.tv.sdk.ui.model.WaterfallModel
import com.fangtang.tv.sdk.ui.net.bean.MovieEntityNlp
import com.fangtang.tv.sdk.ui.page.NlpPageAdapter
import com.fangtang.tv.sdk.ui.utils.CommonUtils
import com.fangtang.tv.support.item2.presenter.CommonItemPresenter
import com.fangtang.tv.support.item2.presenter.SimpleItemPresenter
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.fangtang.tv.waterfall.*
import java.util.concurrent.ConcurrentHashMap

class NlpFragmentPresenter(private val iView: IView) : BaseContract.Presenter(), Waterfall.OnItemClickListener, IPushManager.ReceivePushMessageListener, VoiceHelper.CallBack {

    private var mWaterfall: Waterfall.IPageInterface? = null
    // 分页加载
    private var mPageAdapter: BaseAbstractPageAdapter? = null

    private var mSplitView: View? = null

    // 缓存界面上显示的item 用于优化语音匹配
    private val mCacheDisplayDataList = mutableListOf<WaterfallModel>()

    override fun onStart() {
        super.onStart()
        initWaterfall()

        val data = iView.getNlpData()
        if(data == null){
            logEF("no nlp data")
        }
        CommonUtils.covertData(data?.movie).let {
            it as ArrayList
            it.add(0, WaterfallModel(Constant.ITEM_TTS, Constant.SPECIAL_HOST_VIEW_WIDTH, Constant.HEIGHT_TTS_VIEW, ROW_COUNT))
            // 渲染数据
            mPageAdapter?.startLoadData(it)
        }

        // 注册指令词回调
        FangTang.getInstance().pushManager.addReceivePushMessageListener(this)
    }

    override fun onStop() {
        super.onStop()
        // 删除指令词回调
        FangTang.getInstance().pushManager.removeReceivePushMessageListener(this)
    }

    private fun initWaterfall() {
        val waterfallPageView = iView.findView<WaterfallPageView>(R.id.waterfall)
        waterfallPageView?.setPadding(DimensUtil.dp2Px(60F), 0, 0, 0)
        val ps = WaterfallPresenterSelector()
        val waterfall = Waterfall.Builder()
                .setWaterfallPresenterSelector(ps)
                .setRecyclerView(waterfallPageView)
                .setOnItemClickListener(this)
//                .setDebug(DebugStatus.isDebug())
                .build()

        mWaterfall = waterfall

        // 注册presenter
        registerPresenter(ps)

        // 不显示loading文字
        waterfall.pageRecyclerView.setLoadingEnable(true)

        // 注册分页逻辑
        mPageAdapter = NlpPageAdapter()
        // 初始化分页
        mPageAdapter?.setUp(waterfall, ROW_COUNT)

//        // 设置默认焦点逻辑
        waterfall.setFocusStrategy(DefaultFocusStrategy(waterfallPageView))

        // 初始化页面角标更新类
        VoiceHelper(waterfallPageView).setCallback(this)

        mWaterfall?.pageRecyclerView?.recyclerView?.parent?.parent?.let {
            it as ViewGroup

            mSplitView = View(iView.getContext())
            mSplitView?.setBackgroundColor(Color.parseColor("#25FFFFFF"))
            mSplitView?.alpha = 0F
            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
            params.topMargin = DimensUtil.dp2Px(94F)
            it.addView(mSplitView, params)

            mWaterfall?.pageRecyclerView?.recyclerView?.addOnScrollListener(InnerOnScrollListener())
        }

        // 设置瀑布流顶部的焦点规则
        waterfall.pageRecyclerView.focusDispatcher.onFocusSearchListener = object : AbsFocusDispatcher.OnFocusSearchListenerAdapter() {
            override fun isLockFocusOnSearchFailed(child: View?, focused: View?, childPosition: Int, direction: Int): Boolean {
//                logW("childPosition:$childPosition child:$child focused:$focused")
                // 放开焦点拦截
                if (direction == View.FOCUS_UP && childPosition <= 3) {
                    return false
                }
                return super.isLockFocusOnSearchFailed(child, focused, childPosition, direction)
            }
        }

    }

    private fun registerPresenter(ps: WaterfallPresenterSelector) {
        ps.registerItemPresenter(Constant.ITEM_NO_DATA, ItemNoDataPresenter())
        ps.registerItemPresenter(Constant.ITEM_TTS, ItemTtsPresenter())
        ps.registerItemPresenter(Constant.ITEM_VIDEO, ItemVideoPresenter())
    }

    override fun onNumberIndexChange(holders: MutableList<Presenter.ViewHolder>) {
        mCacheDisplayDataList.clear()
        var index = 1
        holders.forEach { holder ->
            val facet = holder.getFacet(FlowLayout.ItemFacet::class.java) as FlowLayout.ItemFacet
            val presenter = facet.presenter
            val bean = facet.item as WaterfallModel
            // 影人界面顶部的特殊View需要过滤掉，因为它不需要Index角标
            if (presenter is CommonItemPresenter) {
                bean.displayIndex = index
                presenter.updateNumberIndex(holder, index)
                index++
                mCacheDisplayDataList.add(bean)
            }
        }
        // 将当前界面显示的item注册到指令
        regTitleCommand()
    }

    override fun onNumberIndexClear(holders: MutableList<Presenter.ViewHolder>) {
        holders.forEach { holder ->
            val facet = holder.getFacet(FlowLayout.ItemFacet::class.java) as FlowLayout.ItemFacet
            val presenter = facet.presenter
            if (presenter is SimpleItemPresenter) {
                presenter.updateNumberIndex(holder, 0)
            }
        }
    }

    private fun regTitleCommand() {
        if (mCacheDisplayDataList.size > 0) {
            val commands = ConcurrentHashMap<String, String>()
            mCacheDisplayDataList.forEach {
                commands[it.name] = it.customData
            }
            val className = NlpActivity::class.java.name
            FangTang.getInstance().nlpManager.registerVoiceViewCache(className, commands)
        }
    }

    private inner class InnerOnScrollListener : RecyclerView.OnScrollListener() {
        private var y = 0
        private var limitY = 30F
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//            if (limitY == 0F) {
//                limitY = mSplitView.y + mSplitView.height
//            }

            y += dy

            if (y > limitY && mSplitView?.alpha == 0F) {
                showTabList()
            } else if (y <= limitY && mSplitView?.alpha == 1F) {
                hideTabList()
                // 矫正一下y
                y = 0
            }
        }

        private fun showTabList() {
            mSplitView?.alpha = 1F
        }

        private fun hideTabList() {
            mSplitView?.alpha = 0F
        }
    }

    override fun onItemClick(v: View?, model: ItemModel?, component: ComponentModel<*>?, section: SectionModel<out ComponentModel<*>>?) {
        model as WaterfallModel
        FangTang.getInstance().routerManager.launchIntent(model.customData)
        CommonUtils.commitHistory(model.cid, model.movieId)
    }

    override fun onReceivePushMessage(pushMessage: PushMessage) {
        if(pushMessage.messageType == PushMsgType.PUSH_MSG_COMMAND){
            val handled = CommonUtils.dispatchVoiceCommand(this, pushMessage.messageData as VoiceCommand)
            logW { "指令处理:$handled" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPageAdapter?.onDestroy()
        mSplitView?.alpha = 0F
        mCacheDisplayDataList.clear()
    }

    fun nextPage() {
        mWaterfall?.pageRecyclerView?.scrollToNextPage()
    }

    fun prePage() {
        mWaterfall?.pageRecyclerView?.scrollToPreviousPage()
    }

    fun playWithIndex(index: Int) {
        if (index >= 1 && index <= mCacheDisplayDataList.size) {
            val model = mCacheDisplayDataList[index - 1]
            FangTang.getInstance().routerManager.launchIntent(model.customData)
            CommonUtils.commitHistory(model.cid, model.movieId)
        }
    }

    interface IView : BaseContract.IView {
        fun getContext(): Context?
        fun getNlpData(): MovieEntityNlp?
    }
}