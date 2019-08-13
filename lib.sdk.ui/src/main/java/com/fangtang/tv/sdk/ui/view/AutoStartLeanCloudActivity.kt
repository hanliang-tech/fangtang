package com.fangtang.tv.sdk.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.ui.base.BaseActivity


@SuppressLint("Registered")
open class AutoStartLeanCloudActivity : BaseActivity(), IPushManager.ReceivePushMessageListener {

    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHandler.removeCallbacksAndMessages(null)

        // 8秒后初始化LeanCloud
        mHandler.postDelayed({
            //初始化push
            FangTang.getInstance().pushManager.open(null)
        }, DateUtils.SECOND_IN_MILLIS * 8)

        FangTang.getInstance().pushManager.addReceivePushMessageListener(this)
    }

    override fun onReceivePushMessage(pushMessage: PushMessage) {
        if (pushMessage.messageType == PushMsgType.PUSH_IOT_BIND) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        FangTang.getInstance().pushManager.removeReceivePushMessageListener(this)
    }
}