package com.sunrain.ui

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.multidex.MultiDex
import android.text.format.DateUtils
import android.util.Log
import com.fangtang.tv.sdk.FangTangSDK




/**
 * @author WeiPeng
 * @version 1.0
 * @title DemoApp.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/07/10 21:01
 * @changeRecord [修改记录] <br/>
 */
class DemoApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化SDK
        FangTangSDK.get().init(this, "dev")
//        FangTangSDK.get().

//        FangTangSDK.get().query("刘德华的电影")
//        FangTangSDK.get().query("刘德华的电影", object :FangTangSDK.IQueryCallBack{
//            override fun onQuery(bean: NLPBean) {
//
//            }
//
//        })

        Handler(Looper.getMainLooper()).let {loop(it)}

    }

    private fun loop(handler:Handler){
        handler.postDelayed({
            Log.e("sunrain", "${getProcessName_()}")
            loop(handler)
        }, DateUtils.SECOND_IN_MILLIS)
    }

    private fun getProcessName_(): String {
        val mActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT > 20) {
            return mActivityManager.runningAppProcesses[0].processName
        } else {
            return mActivityManager.getRunningTasks(1)[0].topActivity.packageName
        }
    }


}