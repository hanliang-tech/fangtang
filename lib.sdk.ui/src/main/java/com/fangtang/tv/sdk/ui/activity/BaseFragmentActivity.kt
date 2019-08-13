package com.fangtang.tv.sdk.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v4.app.Fragment
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.BaseActivity


@SuppressLint("Registered")
open class BaseFragmentActivity: BaseActivity() {

    override fun onStart() {
        super.onStart()
        refreshView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        refreshView()
    }

    protected fun refreshView(){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_content, getFragment())
        ft.commitAllowingStateLoss()
    }

    open fun getFragment(): Fragment? {
        return null
    }

}