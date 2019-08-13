package com.fangtang.tv.sdk.ui.utils

import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.fangtang.tv.gson.GsonManager
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.bean.VoiceCommand
import com.fangtang.tv.sdk.base.logging.logW
import com.fangtang.tv.sdk.base.nlp.bean.InnerMovieBean
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.ui.model.WaterfallModel
import com.fangtang.tv.sdk.ui.net.bean.MovieEntityNlp
import com.fangtang.tv.sdk.ui.net.bean.VoiceBarEntity
import com.fangtang.tv.sdk.ui.presenter.NlpFragmentPresenter
import com.google.gson.JsonElement

object CommonUtils {

    fun covert2MovieEntityNlp(reply: JsonElement?): MovieEntityNlp? {
        val movieEntity = GsonManager.getGson().fromJson(reply, MovieEntityNlp::class.java)
        // 没有数据
        if (movieEntity == null || movieEntity.movie.isNullOrEmpty()) {
//            Toast.makeText(UpgradeManager.sContext, "没有找到资源呢", Toast.LENGTH_SHORT).show()
            return null
        }
        return movieEntity
    }

    /**
     * 将网络数据转换成瀑布流数据
     */
    fun covertData(any: List<*>?): List<WaterfallModel> {
        if (any == null || any.isEmpty()) {
            return ArrayList()
        }
        return when (any[0]) {
            is InnerMovieBean -> {
                mapperVideo(any as List<InnerMovieBean>)
            }
            else -> ArrayList()
        }
    }

    private fun mapperVideo(movie: List<InnerMovieBean>): List<WaterfallModel> {
        return movie.mapIndexed { index: Int, bean: InnerMovieBean ->
            val model = WaterfallModel(Constant.ITEM_VIDEO, Constant.ITEM_VIDEO_WIDTH, Constant.ITEM_VIDEO_HEIGHT)
            model.realIndex = index
            model.coverUrl = bean.cover
            model.name = bean.title
            model.grade = bean.grade
            model.tag = bean.icon
            model.customData = bean.custom_data
            model.originName = bean.origin_name
            model.originIconUrl = bean.origin_icon
            model.packageName = bean.package_name

            model.cid = bean.cid
            model.movieId = bean.movie_id

            model
        }
    }

    fun handleVoiceBarClick(tip: VoiceBarEntity.Tip) {
        logW { "onItemClicked: $tip" }
        ToastUtils.showShort("演示音箱语音效果")
        val pushMessage = PushMessage()
        pushMessage.messageType = PushMsgType.PUSH_MSG
        pushMessage.messageData = tip.key
        FangTang.getInstance().pushManager.notifyPushMessage(pushMessage)
    }

    /**
     * 语音指令分发
     */
    fun dispatchVoiceCommand(presenter: NlpFragmentPresenter?, cmd: VoiceCommand): Boolean {
        var handled = false
        when (cmd.functionType) {
            // 翻页
            512 -> {
                presenter?.nextPage()
                handled = true
            }
            1024 -> {
                presenter?.prePage()
                handled = true
            }

            // 第几个
            32,
            16,
            4,
            128,
            64 -> {  // 第几个
                presenter?.playWithIndex(cmd.index)
                handled = true
            }

            // 名称
            -1 -> {
                if (!TextUtils.isEmpty(cmd.nlpJson)) {
                    FangTang.getInstance().routerManager.launchIntent(cmd.nlpJson)
                    handled = true
                }
            }

            // 返回 不处理
            65536 -> {
            }
        }
        return handled
    }

    fun isFindDeviceSwitchOn(): Boolean {
        return FangTang.getInstance().kvManager.getBoolean(Constant.KEY_IOT_SEARCH_SWITCH, true)
    }

    fun commitHistory(cid: String?, movieId: String?) {
        FangTang.getInstance().nlpManager.postHistory(cid, movieId)
    }

}