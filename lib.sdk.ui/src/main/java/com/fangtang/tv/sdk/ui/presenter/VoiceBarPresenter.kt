package com.fangtang.tv.sdk.ui.presenter

import android.app.Activity
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.BaseContract
import com.fangtang.tv.sdk.ui.net.bean.VoiceBarEntity
import com.fangtang.tv.sdk.ui.tips.AIFuncView
import com.fangtang.tv.sdk.ui.tips.AIFuncViewListener
import com.fangtang.tv.sdk.ui.utils.CommonUtils
import java.util.concurrent.ConcurrentHashMap

class VoiceBarPresenter(activity: Activity): BaseContract.Presenter() {

    private var aiFuncView = activity.findViewById<AIFuncView>(R.id.ai_funcview)

    fun loadData(page: String) {
        aiFuncView?.okUpdate(LinkedHashMap(), page, object : AIFuncViewListener {
            override fun onItemClicked(tip: VoiceBarEntity.Tip) {
                CommonUtils.handleVoiceBarClick(tip)
            }

            override fun onRenderTip(map: ConcurrentHashMap<String, String>) {
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        aiFuncView.release()
    }
}