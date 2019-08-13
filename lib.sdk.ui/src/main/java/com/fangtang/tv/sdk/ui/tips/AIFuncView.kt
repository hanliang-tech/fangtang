package com.fangtang.tv.sdk.ui.tips

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.net.KDefaultCallBack
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.net.API
import com.fangtang.tv.sdk.ui.net.bean.VoiceBarEntity
import com.fangtang.tv.sdk.ui.tips.util.Utils
import kotlinx.android.synthetic.main.view_aiwm_common.view.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

open class AIFuncView : FrameLayout {

    companion object {
        @JvmField
        var sDEBUG = false
        @JvmField
        var sHideVoiceBar = false
        @JvmField
        var sIsInVoiceHelp = false
        const val TAG = "AIFuncView"
    }

    private var headView: ImageView? = null

    private var tipGroupLayout: TipGroupLayout? = null

    private val renderList = mutableListOf<VoiceBarEntity.Tip>()

    private var listener: AIFuncViewListener? = null

    private object sHandler : Handler(Looper.getMainLooper())

    private var mCacheMap = HashMap<String, CacheBean>(5)

    private var delayTime = 0L

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        // 不需要显示VoiceBar
        if (sHideVoiceBar) {
            post {
                try {
                    val p = parent
                    if (p != null) {
                        p as ViewGroup
                        p.removeView(this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return
        }

        val view = LayoutInflater.from(context).inflate(getLayoutID(), this, false)

        val params = LayoutParams(LayoutParams.MATCH_PARENT, Utils.dp2px(context, 86))
        if (!sIsInVoiceHelp) {
            params.leftMargin = Utils.dp2px(context, 28)
            params.bottomMargin = Utils.dp2px(context, 4)
        }

        addView(view, params)
        //logE("addView")
        initView(context)
        //logE("initView")
    }

    protected open fun getLayoutID(): Int {
        return R.layout.view_aiwm_common
    }

    protected open fun initView(context: Context) {
        headView = findViewById(R.id.caton_view)
        tipGroupLayout = findViewById(R.id.tipGroupLayout)
        headView?.setImageResource(R.drawable.pic_head_ft)
    }

    private fun setVoiceBarTitle(name: String?) {
        if (TextUtils.isEmpty(name)) {
            tvSayWords1.text = "你可以说"
            tvSayWords2.text = ""
        } else {
            if (name!!.contains("|")) {
                val names = name.split("|")
                tvSayWords1.text = names[0]
                tvSayWords2.text = names[1]
            } else {
                tvSayWords1.text = name
                tvSayWords2.text = ""
            }
        }
    }

    /**
     * 设置tips
     * @param map tips集合
     * @param cid 中间件获取平台信息
     * @param listener 回调 {@link com.fangtang.tv.voicehelpexpandview.AIFuncViewListener}
     */
    fun okUpdate(map: LinkedHashMap<String, String>?, cid:String, listener: AIFuncViewListener) {
        sHandler.removeCallbacksAndMessages(null)
        sHandler.postDelayed(TipRunnable(
                this, map, cid, listener
        ), delayTime)
    }

    fun justShow(tips:List<VoiceBarEntity.Tip>){
        tipGroupLayout?.showTips(tips)
    }

    fun setItemFocusChangeListener(listener: OnItemFocusChangeListener?) {
        tipGroupLayout?.setItemFocusChangeListener(listener)
    }

    /**
     * 释放资源
     */
    fun release() {
        logD { "release" }
        tipGroupLayout?.release()
        renderList.clear()
        mCacheMap.clear()
//        CartonSupport.exit()
//        catonView?.exit()
    }

    private inner class TipRunnable(
            val funcView: AIFuncView,
            val map: HashMap<String, String>?,
            val cid: String,
            val listener: AIFuncViewListener) : Runnable {
        override fun run() {
            // 如果是空 -- 清空显示 -- 无需请求网络
            if (map == null) {
                //logE("clear tips")
                funcView.dealData(map)
                funcView.listener = null
                return
            }
            if (!TextUtils.isEmpty(cid) && funcView.mCacheMap.containsKey(cid)) {
                //logE("use cache")
//                Log.e("Less", "语音bar已经被Cache 不在继续进行")
                val bean = funcView.mCacheMap[cid]

                setVoiceBarTitle(bean!!.tip)
                funcView.dealData(bean.data)
                listener.onRenderTip(list2map(bean.data!!))
                return
            }
            funcView.listener = listener

            val list = map2list(map)

//            FangTang.getInstance().
            API.reqVoiceBarTips(cid, KDefaultCallBack { code, entity ->
                try {
                    if (code == 200 && entity?.data != null) {
                        setVoiceBarTitle(entity.data?.tip)

                        entity.data?.words?.run {
                            list?.addAll(0, this)
                            forEach { map[it.key] = it.value }
                        }

                        funcView.mCacheMap[cid] = CacheBean(entity.data?.tip, list)
                    } else {
                        setVoiceBarTitle(null)
                    }
                } catch (e: Exception) {
                    setVoiceBarTitle(null)
                } finally {
                    // 渲染界面 && 反馈给大耳朵
                    sHandler.post {

                        funcView.dealData(list)

                        val concurrentMap = ConcurrentHashMap<String, String>()
                        concurrentMap.putAll(map)

                        listener.onRenderTip(concurrentMap)
                    }
                }
            })
        }

        private fun list2map(list: ArrayList<VoiceBarEntity.Tip>): ConcurrentHashMap<String, String> {
            val hashMap = ConcurrentHashMap<String, String>(list.size)
            list.forEach {
                hashMap[it.key] = it.value
            }
            return hashMap
        }

        private fun map2list(map: HashMap<String, String>?): ArrayList<VoiceBarEntity.Tip>? {
            var list: ArrayList<VoiceBarEntity.Tip>? = null

            map?.let { m ->
                list = ArrayList(m.size)
                m.keys.forEach { k ->
                    list!!.add(VoiceBarEntity.Tip(k, m[k]!!, 0))
                }
            }
            return list
        }

    }

    fun disableFocusFilter() {
        tipGroupLayout?.nextFocusLeftId = View.NO_ID
        tipGroupLayout?.nextFocusRightId = View.NO_ID
    }

    private fun dealData(map: ArrayList<VoiceBarEntity.Tip>?) {
        //logE("dealData")
        renderList.clear()
        map?.let { renderList.addAll(it) }
        updateTip()
    }

    private fun getAppVersionName(context: Context): Int {
        var versionCode: Int = -1
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            versionCode = pi.versionCode
        } catch (e: Exception) {
        }
        return versionCode
    }

    private fun hideTipsView() {
        tipGroupLayout?.visibility = View.GONE
    }

    private fun showTipsView() {
        tipGroupLayout?.visibility = View.VISIBLE
    }

    private fun updateTip() {
        initAdapterIfNeed()
    }

    private fun initAdapterIfNeed() {
        tipGroupLayout?.setOnClickListener { view ->
            if (Utils.checkLastTime()) {
                listener?.let {
                    val bean = view.getTag(R.id.id_tip_bean) as VoiceBarEntity.Tip
                    it.onItemClicked(bean)
                }
            }
        }
        //logE("tell show tips")
        tipGroupLayout?.showTips(renderList)
    }

}

interface AIFuncViewListener {
    fun onItemClicked(tip: VoiceBarEntity.Tip)
    fun onRenderTip(map: ConcurrentHashMap<String, String>)
}

data class CacheBean(var tip: String?, var data: ArrayList<VoiceBarEntity.Tip>?)
