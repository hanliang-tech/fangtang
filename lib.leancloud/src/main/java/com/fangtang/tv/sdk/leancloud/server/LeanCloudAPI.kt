package com.fangtang.tv.sdk.leancloud.server

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.push.PushConfigBean
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LeanCloudAPI {

    @POST("/sdk/v1/config/leancloud")
    @FormUrlEncoded
    fun getLeanCloudConfig(@FieldMap params: Map<String, String>): Call<StatusBean<PushConfigBean>>
}