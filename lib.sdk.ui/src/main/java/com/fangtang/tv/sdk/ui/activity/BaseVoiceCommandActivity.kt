package com.fangtang.tv.sdk.ui.activity

import android.annotation.SuppressLint
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.bean.VoiceCommand
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType

@SuppressLint("Registered")
open class BaseVoiceCommandActivity: BaseFragmentActivity(),IPushManager.ReceivePushMessageListener {

    override fun onResume() {
        super.onResume()
        FangTang.getInstance().pushManager.addReceivePushMessageListener(this)
    }

    override fun onReceivePushMessage(pushMessage: PushMessage) {
        if(pushMessage.messageType == PushMsgType.PUSH_MSG_COMMAND){
            val voiceCmd = pushMessage.messageData as VoiceCommand
            if(voiceCmd.saveTxt?.contains("返回") == true){
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        FangTang.getInstance().pushManager.removeReceivePushMessageListener(this)
    }

}