package com.fangtang.tv.sdk.base.remote.server

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindStatus
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RemoteAPI {

    @POST("/sdk/v1/nlp/getbindinfo")
    @FormUrlEncoded
    fun bindDeviceInfo(@FieldMap params: Map<String, String>): Call<StatusBean<RemoteDeviceBindInfo>>


    @POST("/sdk/v1/nlp/getbindcode")
    @FormUrlEncoded
    fun bindDeviceCode(@FieldMap params: Map<String, String>): Call<StatusBean<RemoteDeviceBindCode>>


    @POST("/sdk/v1/nlp/unbind")
    @FormUrlEncoded
    fun unbindDevice(@FieldMap params: Map<String, String>): Call<StatusBean<RemoteDeviceBindStatus>>
}