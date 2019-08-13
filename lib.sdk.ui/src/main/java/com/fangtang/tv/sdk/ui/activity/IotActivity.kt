package com.fangtang.tv.sdk.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import com.fangtang.tv.sdk.ui.fragment.IotFragment

class IotActivity : BaseVoiceBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadBarData("888")
    }

    override fun getFragment(): Fragment? {
        return IotFragment()
    }
}