package com.fangtang.tv.sdk.base.util

import android.app.ActivityManager
import android.content.Context
import com.blankj.utilcode.util.AppUtils


object TaskUtils {

    fun getTopActivityPackageName(context: Context): String {
        var topActivityPackage: String? = null
        val activityManager = context.applicationContext
                .getSystemService(android.content.Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfos = activityManager
                .getRunningTasks(1)
        if (runningTaskInfos != null) {
            val f = runningTaskInfos[0].topActivity
            topActivityPackage = f.packageName
        }
        return topActivityPackage!!
    }

    fun getTopActivityName(context: Context): String {
        var topActivityClassName = ""
        try {
            val activityManager = context.applicationContext
                    .getSystemService(android.content.Context.ACTIVITY_SERVICE) as ActivityManager
            val runningTaskInfos = activityManager
                    .getRunningTasks(1)
            if (runningTaskInfos != null) {
                val f = runningTaskInfos[0].topActivity
                topActivityClassName = f.className
            }
        } catch (e: Exception) {
            topActivityClassName = AppUtils.getAppPackageName()
        } finally {
            return topActivityClassName
        }
    }
}