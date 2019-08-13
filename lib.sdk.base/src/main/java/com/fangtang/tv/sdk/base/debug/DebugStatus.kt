package com.fangtang.tv.sdk.base.debug

object DebugStatus {

    private var DEBUG = true

    fun isDebug() : Boolean{
        return DEBUG
    }

    fun setDebug(debug: Boolean){
        DEBUG = debug
    }
}