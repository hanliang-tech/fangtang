package com.fangtang.tv.sdk.base.net


class KDefaultCallBack<T>(private val callBack: (code: Int, entity: T?) -> Unit) : KCallBack<T>() {

    override fun onSuccess(t: T?) {
        try {
            callBack(200, t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFail(code: Int, msg: String?) {
        try {
            callBack(-1, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}