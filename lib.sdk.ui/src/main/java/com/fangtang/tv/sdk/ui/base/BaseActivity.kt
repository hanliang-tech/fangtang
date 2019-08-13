package com.fangtang.tv.sdk.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.fangtang.tv.base.activity.FBaseActivity
import com.fangtang.tv.sdk.ui.utils.ScreenAdapterUtil

@SuppressLint("Registered")
open class BaseActivity : FBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenAdapterUtil.setCustomDensityFixedWidth(this, this.application, 1280F)
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

//    override fun onStart() {
//        super.onStart()
//
//        createView()
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//        createView()
//    }

//    open fun createView(){}

}