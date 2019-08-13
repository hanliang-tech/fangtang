package com.fangtang.tv.sdk.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.ui.fragment.NlpFragment

class NlpActivity : BaseVoiceBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBarData("nlp")

        FangTang.getInstance().nlpManager.registerVoiceSystemCache(this::class.java.name)
    }

    override fun getFragment(): Fragment? {
        return NlpFragment()
    }
}