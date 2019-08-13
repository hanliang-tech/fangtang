package com.fangtang.tv.sdk.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.presenter.VoiceBarPresenter

@SuppressLint("Registered")
open class BaseVoiceBarActivity : BaseVoiceCommandActivity() {

    private var voiceBarPresenter: VoiceBarPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        voiceBarPresenter = VoiceBarPresenter(this)
    }

    protected fun loadBarData(pageId: String) {
        voiceBarPresenter?.loadData(pageId)
    }

    override fun onDestroy() {
        super.onDestroy()

        voiceBarPresenter?.onDestroy()
    }
}