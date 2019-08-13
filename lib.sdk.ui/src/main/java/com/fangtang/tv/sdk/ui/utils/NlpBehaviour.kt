package com.fangtang.tv.sdk.ui.utils

import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.ToastUtils
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.FangTangSDK
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.logging.logEF
import com.fangtang.tv.sdk.base.logging.logI
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.nlp.INLPManager
import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.google.gson.JsonElement


object NlpBehaviour {

    fun handleQuery(query: String?, extras: Any?, callBack: FangTangSDK.IQueryCallBack?) {

        logD { "new query ----------------------------------------> $query  extras: $extras" }

        if (query == null) return

        when (query) {

            "main_view",
            "这是来自小爱AI面板的主动请求",
            "query这是来自小爱AI面板的主动请求query" -> {
//                if (!AppManager.getInstance().isOnTop()) {
//                    AppManager.getInstance().pullMain2Foreground()
//                }

                return
            }

            "切换至预览环境" -> {
                BaseRest.isPreview = true
//                OldRest.isPreview = true
                ToastUtils.showLong("已切换至预览模式")
                return
            }
        }
//
//            // 特殊处理 返回
//            "返回到主页",
//            "返回主页",
//            "返回到首页",
//            "返回首页" -> {
////                val backCmd = com.fangtang.tv.sdk.base.bean.VoiceCommand()
////                backCmd.functionType = if (AppManager.getInstance().isOnTop()) {
////                    65536
////                } else {
////                    -1
////                }
////                backCmd.saveTxt = "返回"
////                dispatchCommand(backCmd)
//                return
//            }
//
//            // 新增调试
//            "调试信息" -> {
//                Utils.getApp().sendBroadcast(
//                        Intent("com.sunrain.test.BRIDGE")
//                                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//                                .putExtra("cmd", "debug")
//                )
//                return
//            }
//
//        }

        // 解析extra
        var showToast = true
        var params = ""
        (extras as JsonElement?)?.let {
            showToast = it.asJsonObject.get("show_tts").asBoolean
            params = it.asJsonObject.get("additional_params").toString()
        }
//        if (showToast && AppManager.getInstance().isOnTop()) {
//            Toast.makeText(UpgradeManager.sContext, query, Toast.LENGTH_SHORT).show()
//        }

        FangTang.getInstance().nlpManager.query(query, params, object : INLPManager.NLPQueryListener {
            override fun onSuccess(nlpBean: NLPBean) {
                if (!FangTangSDK.get().getSdkInterceptor().handleQueryResult(nlpBean)) {
                    logD { "reqNlp: $nlpBean" }
                    try {
                        val result = dealWithDomain(nlpBean)
                        if(result == 0) callBack?.onQuery(nlpBean)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onError(e: java.lang.Exception) {
                e.printStackTrace()
                logD { "${e.message}" }
            }

        })
    }

    private fun dealWithDomain(nlp: NLPBean): Int {

        fillVoiceStatus(nlp)
        logI { "status:" + VoiceStatus.toStrings() }

        when (nlp.domain) {
            Constant.NLP.DOMAIN_MOVIE -> parseMovie(nlp)
            Constant.NLP.DOMAIN_CHAT -> {
            }
            else -> {
                logEF("不支持的domain:${nlp.domain}")
                return -1
            }
        }

        return 0
    }

    private fun parseMovie(nlp: NLPBean) {
        when (nlp.intention) {
            Constant.NLP.INTENTION_SEARCH,
            Constant.NLP.INTENTION_SEARCH_PLUS -> showMovie(nlp.content.reply)
            Constant.NLP.INTENTION_LIVE -> skip2page(nlp.content.reply)
            else -> logEF("不支持的intention:${nlp.intention}")
        }
    }

    private fun fillVoiceStatus(data: NLPBean) {
        VoiceStatus.query = data.query
        VoiceStatus.domain = data.domain
        VoiceStatus.queryId = data.query_id

        VoiceStatus.intention = data.intention
        VoiceStatus.display = data.content.display
        VoiceStatus.tts = data.content.tts
    }

    private fun skip2page(reply: JsonElement) {
        val skip = reply.asJsonObject.get("skip_to_page").asJsonArray[0].toString()
        FangTang.getInstance().routerManager
                .launchIntent(skip)
    }

    private fun showMovie(reply: JsonElement) {
        val movieEntity = CommonUtils.covert2MovieEntityNlp(reply)
        movieEntity?.let {
            if (it.movie.size == 1) {
                FangTang.getInstance().routerManager.launchIntent(it.movie[0].custom_data)
                CommonUtils.commitHistory(it.movie[0].cid, it.movie[0].movie_id)
                return
            }
//            // 把数据丢给中间层
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fangtang://skip2page/nlp"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("data", movieEntity)
            FangTang.getInstance().applicationContext.startActivity(intent)
        }
    }
}