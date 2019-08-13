package com.fangtang.tv.sdk.leancloud

import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.AVIMMessageHandler
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.google.gson.Gson


class LeanCloudMessageHandler(private val leanCloudManager: LeanCloudManager,
                              private val gson: Gson) : AVIMMessageHandler() {

    override fun onMessage(message: AVIMMessage?, conversation: AVIMConversation?, client: AVIMClient?) {
        super.onMessage(message, conversation, client)
        if (message == null) return

        val msg = message.content
        logD { "push msg:$msg" }
        try {
            val leanCloudBean = gson.fromJson(msg, LeanCloudBean::class.java)

            val msg = leanCloudBean._lctext ?: return
            val msgType = leanCloudBean._lctype
            val attributes = leanCloudBean._lcattrs

            val pushMessage = PushMessage()

            when (msgType) {
                // 微信绑定成功
                LeanCloudMessageType.MESSAGE_TYPE_WECHAT_BIND_SUCCESS -> {
                    pushMessage.messageType = PushMsgType.PUSH_MSG_BIND
                }
                // 音箱绑定成功
                LeanCloudMessageType.MESSAGE_TYPE_IOT_BIND_SUCCESS ->{
                    pushMessage.messageType = PushMsgType.PUSH_IOT_BIND
                }
                // 音箱解绑
                LeanCloudMessageType.MESSAGE_TYPE_IOT_UNBIND_SUCCESS ->{
                    pushMessage.messageType = PushMsgType.PUSH_IOT_UNBIND
                }
                //key event
                LeanCloudMessageType.MESSAGE_TYPE_KEY_EVENT -> {
                    if (msg.startsWith(LeanCloudConstants.MESSAGE_VOICE_KEY)) {
                        pushMessage.messageType = PushMsgType.PUSH_MSG_KEY_EVENT
                        val keyJson = msg.replace(LeanCloudConstants.MESSAGE_VOICE_KEY, "")
                        pushMessage.messageData = keyJson

                        leanCloudManager.updateLastUseTime()
                    }
                }
                // 主要的通讯消息
                LeanCloudMessageType.MESSAGE_TYPE_QUERY -> {
                    if (msg.startsWith(LeanCloudConstants.MESSAGE_QUERY_KEY)) {
                        pushMessage.messageType = PushMsgType.PUSH_MSG
                        val result = msg.replace(LeanCloudConstants.MESSAGE_QUERY_KEY, "")
                        pushMessage.messageData = result
                        pushMessage.obj = attributes

                        leanCloudManager.updateLastUseTime()
                    }
                }
                // 指令
                LeanCloudMessageType.MESSAGE_TYPE_COMMAND -> {
                    pushMessage.messageType = PushMsgType.PUSH_MSG
                    pushMessage.messageData = msg

                    leanCloudManager.updateLastUseTime()
                }
                else -> {
                    return
                }
            }
            leanCloudManager.notifyPushMessage(pushMessage)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}