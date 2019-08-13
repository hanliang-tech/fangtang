package com.fangtang.tv.sdk.base.app.server

import com.fangtang.tv.sdk.base.app.AppInfoListBean
import com.fangtang.tv.sdk.base.bean.ResultBean
import com.fangtang.tv.sdk.base.bean.StatusBean
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface AppInfoAPI {

    @POST("/sdk/v1/app/index")
    @FormUrlEncoded
    fun getApkList(@FieldMap params: Map<String, String>): Call<StatusBean<AppInfoListBean>>

    @POST("/sdk/v1/app/uploadpackage")
    @FormUrlEncoded
    fun reportInstalledApkList(@FieldMap params: Map<String, String>): Call<StatusBean<ResultBean>>

    @POST("/sdk/v1/config/checkapplist")
    @FormUrlEncoded
    fun checkAppList(@FieldMap params: Map<String, String>): Call<StatusBean<List<String>>>
}