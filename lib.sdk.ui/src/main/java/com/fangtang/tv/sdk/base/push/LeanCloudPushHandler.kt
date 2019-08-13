package com.fangtang.tv.sdk.base.push

import android.widget.Toast
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.FangTangSDK
import com.fangtang.tv.sdk.ui.utils.NlpBehaviour


class LeanCloudPushHandler : IPushManager.ReceivePushMessageListener {

    override fun onReceivePushMessage(pushMessage: PushMessage?) {
        if (pushMessage == null) return

        when (pushMessage.messageType) {
            PushMsgType.PUSH_MSG_QUERY -> {
                val query = pushMessage.messageData as String
                val extras = pushMessage.obj

                if (!FangTangSDK.get().getSdkInterceptor().handleBeforeQuery(query, extras)) {
                    NlpBehaviour.handleQuery(query, extras, null)
                }
            }
            PushMsgType.PUSH_MSG_BIND -> {
                if (!FangTangSDK.get().getSdkInterceptor().handleDeviceBind()) {
                    Toast.makeText(FangTang.getInstance().applicationContext, "绑定成功！", Toast.LENGTH_SHORT).show()
                }
            }
            PushMsgType.PUSH_MSG_COMMAND -> { // 语音指令
//                CommonUtils.dispatchVoiceCommand()
//                NlpBehaviour.dispatchCommand(pushMessage.messageData as VoiceCommand)
            }
        }
    }
}