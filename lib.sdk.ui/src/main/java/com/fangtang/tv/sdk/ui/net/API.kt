package com.fangtang.tv.sdk.ui.net

import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.ui.net.bean.VoiceBarEntity
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

object API : BaseRest(FangTang.getInstance().deviceManager) {

    private val api: IApi by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(IApi::class.java)
    }

    /**
     * 请求VoiceBar信息
     */
    fun reqVoiceBarTips(cid: String, callBack: KCallBack<StatusBean<VoiceBarEntity?>?>) {
        api.reqVoiceBarTips(getBaseRequestParams()
                .apply {
                    this["cid"] = cid
                }).enqueue(callBack)
    }

    interface IApi {

        @POST("/sdk/v1/config/tipword")
        @FormUrlEncoded
        fun reqVoiceBarTips(@FieldMap params: Map<String, String>): Call<StatusBean<VoiceBarEntity?>?>
    }
}