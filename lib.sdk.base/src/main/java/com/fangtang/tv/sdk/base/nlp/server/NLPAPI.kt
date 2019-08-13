package com.fangtang.tv.sdk.base.nlp.server

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPModel
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NLPAPI {

    @POST("/sdk/v1/nlp/query")
    @FormUrlEncoded
    fun query(@FieldMap params: Map<String, String>): Call<StatusBean<NLPBean>>

    @POST("/sdk/v1/nlp/querypage")
    @FormUrlEncoded
    fun queryPage(@FieldMap params: Map<String, String>): Call<StatusBean<NLPPageBean>>

    @POST("/sdk/v1/history/record")
    @FormUrlEncoded
    fun postHistory(@FieldMap params: Map<String, String>): Call<JsonElement>


    @POST("/sdk/v1/config/nlpmodel")
    @FormUrlEncoded
    fun nlpModel(@FieldMap params: Map<String, String>): Call<StatusBean<NLPModel>>

}