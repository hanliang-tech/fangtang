package com.fangtang.tv.sdk.base.logging

import android.util.Log
import com.fangtang.tv.sdk.base.debug.DebugStatus


var LOG_TAG = ""

private val VERBOSE = Log.VERBOSE
private val DEBUG = Log.DEBUG
private val INFO = Log.INFO
private val WARN = Log.WARN
private val ERROR = Log.ERROR
private val ASSERT = Log.ASSERT

fun logV(msgBlock: () -> String) {
    if (DebugStatus.isDebug()) Log.println(VERBOSE, LOG_TAG, dealMsg(msgBlock()))
}

fun logD(msgBlock: () -> String) {
    if (DebugStatus.isDebug()) Log.println(DEBUG, LOG_TAG, dealMsg(msgBlock()))
}

fun logI(msgBlock: () -> String) {
    if (DebugStatus.isDebug()) Log.println(INFO, LOG_TAG, dealMsg(msgBlock()))
}

fun logDF(msg: String) {
    Log.println(DEBUG, LOG_TAG, dealMsg(msg))
}

fun logW(msgBlock: () -> String) {
    if (DebugStatus.isDebug()) Log.println(WARN, LOG_TAG, dealMsg(msgBlock()))
}

fun logE(msgBlock: () -> String) {
    if (DebugStatus.isDebug()) Log.println(ERROR, LOG_TAG, dealMsg(msgBlock()))
}

fun logEF(msg: String) {
    Log.println(ERROR, LOG_TAG, dealMsg(msg))
}

private fun dealMsg(msg: String): String {
    val logSb = StringBuilder()
    logSb.append("[T]:").append(Thread.currentThread().name)

    val traces = Thread.currentThread().stackTrace
    for (i in 0 until traces.size) {
        val element = traces[i]
        if (!element.className.startsWith("java.lang")
                && !element.className.startsWith("dalvik.system")
                && !element.className.startsWith("com.fangtang.tv.sdk.base.logging.LogExt")) {
            logSb.append(" (").append(element.fileName).append(":").append(element.lineNumber).append(")")
            break
        }
    }
    return logSb.append(" ---> ").append(msg).toString()
}