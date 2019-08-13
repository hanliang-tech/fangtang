package com.fangtang.tv.sdk.base.net

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open abstract class KCallBack<T> : Callback<T> {

    var extra1: Any? = null
    var extra2: Any? = null

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(response.body())
        } else {
            try {
                onFail(response.code(), response.errorBody()!!.string())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable?) {
        onFail(-1, throwable?.message)
    }

    abstract fun onSuccess(t: T?)

    abstract fun onFail(code: Int, msg: String?)


}