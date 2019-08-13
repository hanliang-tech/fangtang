package com.fangtang.tv.sdk.leancloud

import android.app.Application
import android.text.TextUtils
import android.text.format.DateUtils
import com.avos.avoscloud.AVOSCloud
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessageManager
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.kv.IKVManager
import com.fangtang.tv.sdk.base.logging.FLog
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.logging.logE
import com.fangtang.tv.sdk.base.logging.logI
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushConfigBean
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.RemoteManager
import com.fangtang.tv.sdk.base.remote.bean.Remote
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo
import com.fangtang.tv.sdk.leancloud.LeanCloudConstants.KEY_LAST_USE_DAY
import com.fangtang.tv.sdk.leancloud.server.impl.LeanCloudAPIImpl
import com.google.gson.Gson
import java.util.*

class LeanCloudManager : IPushManager, RemoteManager.RemoteListener {

    companion object {

        private const val TAG = "LeanCloudManager"

        private const val STATE_NONE = 0    // 未初始化
        private const val STATE_CONFIG = 1  // 配置
        private const val STATE_INIT = 1    // 初始化
        private const val STATE_LOGGING = 2  // 正在登录
        private const val STATE_SUCCESS = 3  // 登录成功
        private const val STATE_FAILED = 4  // 登录失败
    }

    private var currentLeanCloudState = STATE_NONE

    private lateinit var application: Application
    private lateinit var kvManager: IKVManager
    private lateinit var remoteManager: IRemoteManager
    private lateinit var deviceManager: DeviceManager
    private lateinit var gson: Gson

    private lateinit var appKey: String
    private lateinit var clientKey: String
    private var debugLogEnabled = false

    private lateinit var pushConfiguration: LeanCloudConfiguration

    private val receivePushMessageListeners: MutableList<IPushManager.ReceivePushMessageListener> = Collections.synchronizedList(mutableListOf())

    @Synchronized
    fun init(pushConfiguration: LeanCloudConfiguration) {
        this.pushConfiguration = pushConfiguration
        this.application = pushConfiguration.context as Application

        this.appKey = pushConfiguration.appKey
        this.clientKey = pushConfiguration.clientKey

        this.kvManager = pushConfiguration.kvManager
        this.remoteManager = pushConfiguration.remoteManager
        this.remoteManager.addRemoteListener(this)
        this.deviceManager = pushConfiguration.deviceManager
        this.debugLogEnabled = pushConfiguration.writeLogs
        this.gson = Gson()

        initLeanCloud()
    }

    private fun initLeanCloud() {
        LeanCloudAPIImpl(deviceManager)
                .getLeanCloudConfig(object : KCallBack<StatusBean<PushConfigBean>>() {
                    override fun onSuccess(data: StatusBean<PushConfigBean>?) {
                        if (FLog.isLoggable(FLog.VERBOSE)) {
                            FLog.v(TAG, "--------getLeanCloudConfig---$data->>>>>")
                        }
                        if (data != null && data.data != null) {
                            intPushServerConfig(data.data)
                            initLeanCloudSDKConfig()
                        } else {
                            initLeanCloudSDKConfig()
                        }
                    }

                    override fun onFail(code: Int, msg: String?) {
                        initLeanCloudSDKConfig()
                    }
                })
    }

    override fun open(callback: IPushManager.PushCallback?) {

        logD { "leancloud state:$currentLeanCloudState" }
        if (currentLeanCloudState == STATE_SUCCESS) return
        AVIMClient.getInstance(deviceManager.uuid).open(
                object : AVIMClientCallback() {
                    override fun done(client: AVIMClient?, e: AVIMException?) {
                        when (e) {
                            null -> {
                                currentLeanCloudState = STATE_SUCCESS
                                logD { "leancloud login success" }
                                if (FLog.isLoggable(FLog.VERBOSE)) {
                                    FLog.v(TAG, "------leancloud login success---->>>>>")
                                }
                                val pushMessage = PushMessage()
                                pushMessage.messageType = PushMsgType.PUSH_MSG_LOGIN
                                pushMessage.messageData = true
                                notifyPushMessage(pushMessage)
                            }
                            else -> {
                                currentLeanCloudState = STATE_FAILED
                                logD { "leancloud login failed" }
                                if (FLog.isLoggable(FLog.VERBOSE)) {
                                    FLog.v(TAG, "------leancloud login error---appCode:${e.appCode}---code:$${e.appCode}----message:${e.message}->>>>>")
                                }
                                val pushMessage = PushMessage()
                                pushMessage.messageType = PushMsgType.PUSH_MSG_LOGIN
                                pushMessage.messageData = false
                                notifyPushMessage(pushMessage)
                            }
                        }
                    }
                })
    }

    override fun close(callback: IPushManager.PushCallback?) {
        AVIMClient.getInstance(deviceManager.uuid).close(object : AVIMClientCallback() {
            override fun done(client: AVIMClient?, e: AVIMException?) {
                when (e) {
                    null -> {
                        currentLeanCloudState = STATE_INIT
                    }
                    else -> {
                        currentLeanCloudState = STATE_INIT
                    }
                }
            }
        })
    }

    override fun onRemoteChanged(remote: Remote) {
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v(TAG, "------leancloud onRemoteChanged---$remote->>>>>")
        }
        if (remote.hasIotDevice() || remote.hasWechat()) {
            //如果还没有完成配置,则等配置完成
            if (currentLeanCloudState < STATE_CONFIG) {
                return
            }
            initLeanCloudSDKConfig()
        }
    }

    override fun onRemoteError(e: Exception) {
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v(TAG, "------leancloud onRemoteError--->>>>>")
        }
    }

    private fun initLeanCloudSDKConfig() {
        if (currentLeanCloudState == STATE_SUCCESS || currentLeanCloudState == STATE_LOGGING) {
            return
        }
        currentLeanCloudState = STATE_LOGGING
        AVOSCloud.setDebugLogEnabled(debugLogEnabled)
        //account
        AVOSCloud.initialize(application, appKey, clientKey)
        AVIMMessageManager.registerDefaultMessageHandler(LeanCloudMessageHandler(this, gson))
    }

    private fun intPushServerConfig(pushConfig: PushConfigBean) {
        pushConfig?.let { data ->

            setLeanCloudServerWithConfig(data)
            autoStartWithConfig(data)

            currentLeanCloudState = STATE_CONFIG
        }
    }

    /**
     * 启动启动LeanCloud的条件
     * 启动逻辑判断顺序：
     * 使用天数 > 绑定设备数 > 页面停留
     */
    private fun autoStartWithConfig(data: PushConfigBean) {
        logI { "开始判断自启规则" }
        // ### 1. 判断使用天数
        val lastTime = kvManager.getLong(KEY_LAST_USE_DAY, 0L)
        val limitTime = (if(TextUtils.isEmpty(data.limitDay)) "10" else data.limitDay ).toInt() * DateUtils.DAY_IN_MILLIS
        val nowTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 超过设置的limitDay 不需要启动
        if (lastTime != 0L && (nowTime - lastTime) >= limitTime) {
            logI { "已超过${data.limitDay}天未使用 不启动" }
            return
        }

//        logI { "lastTime:$lastTime" }
//        logI { "nowTime:$nowTime" }
//        logI { "dis:${nowTime - lastTime}" }

        logI { "获取绑定信息" }
        // ### 2. 判断连接设备数
        remoteManager.getBindDeviceInfo(object : IRemoteManager.RemoteDeviceBindInfoListener {
            override fun onBindInfoSuccess(bindInfo: RemoteDeviceBindInfo) {
                if (bindInfo.isBindAnyDevice) {
                    logI { "主动启动" }
                    open(null)
                } else {
                    logI { "没有绑定设备 不启动" }
                }
            }

            override fun onBindInfoError(e: java.lang.Exception?) {
                logE { "${e?.message}" }
            }

        })
    }

    private fun setLeanCloudServerWithConfig(data: PushConfigBean) {
        HashMap<AVOSCloud.SERVER_TYPE, String?>().let { map ->
            PushConfigBean::class.java.declaredFields.forEach { field ->
                field.name.let { key ->
                    val type = when (key) {
                        AVOSCloud.SERVER_TYPE.API.toString() -> AVOSCloud.SERVER_TYPE.API
                        AVOSCloud.SERVER_TYPE.PUSH.toString() -> AVOSCloud.SERVER_TYPE.PUSH
                        AVOSCloud.SERVER_TYPE.RTM.toString() -> AVOSCloud.SERVER_TYPE.RTM
                        AVOSCloud.SERVER_TYPE.STATS.toString() -> AVOSCloud.SERVER_TYPE.STATS
                        AVOSCloud.SERVER_TYPE.ENGINE.toString() -> AVOSCloud.SERVER_TYPE.ENGINE
                        else -> null
                    }
                    type
                }?.let { type_key -> map[type_key] = field.get(data) as String? }
            }
            map.filter { !TextUtils.isEmpty(it.value) }
        }.forEach { config -> AVOSCloud.setServer(config.key, config.value) }
    }

    fun updateLastUseTime() {
        logI { "更新LeanCloud最后使用时间" }
        val lastTime = kvManager.getLong(KEY_LAST_USE_DAY, 0L)
        val nowTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        if (lastTime != nowTime) {
            kvManager.putLong(KEY_LAST_USE_DAY, nowTime)
        }
    }

    override fun notifyPushMessage(pushMessage: PushMessage) {
        for (listener in receivePushMessageListeners) {
            try {
                listener.onReceivePushMessage(pushMessage)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun removeReceivePushMessageListener(listener: IPushManager.ReceivePushMessageListener?): Boolean {
        return receivePushMessageListeners != null && receivePushMessageListeners.remove(listener)
    }

    override fun addReceivePushMessageListener(listener: IPushManager.ReceivePushMessageListener): Boolean {
        if (!receivePushMessageListeners.contains(listener)) {
            return receivePushMessageListeners.add(listener)
        }
        return false
    }

    override fun release() {
        //close leancloud
        this.close(null)
        this.receivePushMessageListeners.clear()
        this.remoteManager.removeRemoteListener(this)
    }
}