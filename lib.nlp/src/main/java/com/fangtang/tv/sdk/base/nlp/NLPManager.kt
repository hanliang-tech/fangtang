package com.fangtang.tv.sdk.base.nlp

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import com.fangtang.tv.constantplugin.SequenceCode
import com.fangtang.tv.nlp.CacheData
import com.fangtang.tv.nlp.NlpData
import com.fangtang.tv.nlp.SequenceCacheRegister
import com.fangtang.tv.nlp.ViewCacheRegister
import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.bean.VoiceCommand
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.fangtang.tv.sdk.base.nlp.server.impl.NLPAPIImpl
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.base.router.IRouterManager
import com.fangtang.tv.sdk.base.util.TaskUtils
import com.google.gson.JsonElement
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

class NLPManager : INLPManager {

    private lateinit var context: Context
    private lateinit var deviceManager: DeviceManager
    private lateinit var pushManager: IPushManager
    private lateinit var routerManager: IRouterManager
    private lateinit var nlpAPIImpl: NLPAPIImpl

    @Synchronized
    fun init(nlpConfiguration: NLPConfiguration) {
        context = nlpConfiguration.context
        deviceManager = nlpConfiguration.deviceManager
        pushManager = nlpConfiguration.pushManager
        routerManager = nlpConfiguration.routerManager

        nlpAPIImpl = NLPAPIImpl(deviceManager)

        pushManager.addReceivePushMessageListener {
            if (it.messageType == PushMsgType.PUSH_MSG) {
                val value = it.messageData.toString()
                HookGlobalCall().executeSync(
                        value, value, null,
                        false, value,
                        object : DefaultNlpDealListener(it.obj) {
                            override fun voicePriority(): Boolean {
                                return false
                            }

                            override fun onOpen(packageName: String?) {
                            }

                            override fun onBack(nlpData: NlpData) {
                                executeCommand(nlpData)
                            }

                            override fun onRect(rect: Rect?) {
                            }

                            override fun onWriteListEnd(p0: NlpData?) {
                            }

                            override fun onSpeakOK() {
                            }

                            override fun onServiceControl(json: String?) {
                            }

                            override fun onTransport(nlpData: NlpData) {
                                val pushMessage = PushMessage()
                                pushMessage.messageType = PushMsgType.PUSH_MSG_QUERY
                                pushMessage.messageData = nlpData.userTxt
                                pushMessage.obj = extra
                                pushManager.notifyPushMessage(pushMessage)
                            }

                            override fun onAppCache(nlpdata: NlpData?) {
                            }

                            override fun onTipsCache(nlpdata: NlpData?) {
                            }

                            override fun onIntentJson(nlpdata: NlpData) {
                                routerManager.launchIntent(nlpdata.nlpJson)
                            }

                            override fun onNlpFinshLast(nlpdata: NlpData?) {
                            }

                            override fun onMessageOver(packageName: String?) {
                            }

                            override fun getDefaultScore(): Int {
                                return 3
                            }

                            override fun onInvalid(asr: String?) {
                            }

                            override fun getTopClassName(): String {
                                return TaskUtils.getTopActivityName(context)
                            }

                            override fun onAsr(nlpdata: NlpData?) {
                            }

                            override fun onWriteListInit(p0: String?) {
                            }

                            override fun getTopApp(): String {
                                return TaskUtils.getTopActivityPackageName(context)
                            }

                            override fun isSupportBetterAsr(): Boolean {
                                return true
                            }

                            override fun onTTS(nlpdata: NlpData?) {
                            }

                            override fun onResult(nlpdata: NlpData) {
                                executeCommand(nlpdata)
                            }

                            override fun isShutdown(): Boolean {
                                return false
                            }

                            override fun onNlpFinshPre(nlpdata: String?): Boolean {
                                return true
                            }

                            override fun onUnInstall(nlpdata: String?) {
                            }

                            override fun onViewCache(nlpdata: NlpData) {
                                jump(nlpdata.nlpJson)
                            }

                        })
            }
        }
    }

    fun jump(customData: String) {
        if (!TextUtils.isEmpty(customData)) {
            val json = JSONObject(customData)
            val jump = json.getString("jump")
            routerManager.launchIntent(jump)
        }
    }

    private fun executeCommand(data: NlpData) {
        val cmd = VoiceCommand()
        cmd.index = data.index
        cmd.flag = data.flag
        cmd.functionType = data.functionType
        cmd.sequence = data.sequence
        cmd.nlpJson = data.nlpJson
        cmd.saveTxt = data.saveTxt

        val pushMessage = PushMessage()
        pushMessage.messageType = PushMsgType.PUSH_MSG_COMMAND
        pushMessage.messageData = cmd
        pushManager.notifyPushMessage(pushMessage)
    }

    override fun registerVoiceSystemCache(key: String) {
        SequenceCacheRegister.addSystemCache(
                key,
                SequenceCode.TYPE_NUM or SequenceCode.TYPE_PAGE or SequenceCode.TYPE_BACK
        )
    }

    override fun registerVoiceViewCache(className: String, cacheMap: ConcurrentHashMap<String, String>) {
        ViewCacheRegister.addCacheData(
                CacheData(
                        className,
                        cacheMap,
                        false,
                        className,
                        -1
                )
        )
    }

    override fun query(query: String, attributes: String, listener: INLPManager.NLPQueryListener?) {
        nlpAPIImpl.query(query, attributes, object : KCallBack<StatusBean<NLPBean>>() {
            override fun onSuccess(t: StatusBean<NLPBean>?) {
                if (t?.data != null) {
                    listener?.onSuccess(t.data)
                } else {
                    listener?.onError(Exception("query result is null.. [${t?.msg}]"))
                }
            }

            override fun onFail(code: Int, msg: String?) {
                    msg?.let {
                        listener?.onError(Exception(msg))
                    }
            }
        })
    }

    override fun queryPage(page: Int, queryId: String?, listener: INLPManager.NLPPageListener?) {
        nlpAPIImpl.queryPage(page, queryId, object : KCallBack<StatusBean<NLPPageBean>>(){
            override fun onSuccess(t: StatusBean<NLPPageBean>?) {
                if(t?.data != null){
                    listener?.onSuccess(t.data)
                }else{
                    listener?.onError(Exception("query result is null.."))
                }
            }

            override fun onFail(code: Int, msg: String?) {
                msg?.let {
                    listener?.onError(Exception(msg))
                }
            }

        })
    }

    override fun postHistory(cid: String?, movieId: String?) {
        nlpAPIImpl.postHistory(cid, movieId, object : KCallBack<JsonElement>(){
            override fun onSuccess(t: JsonElement?) {
            }

            override fun onFail(code: Int, msg: String?) {
            }

        })
    }

    override fun release() {

    }
}