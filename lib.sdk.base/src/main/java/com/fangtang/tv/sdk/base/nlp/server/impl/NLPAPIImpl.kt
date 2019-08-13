package com.fangtang.tv.sdk.base.nlp.server.impl

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPModel
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.fangtang.tv.sdk.base.nlp.server.NLPAPI
import com.google.gson.JsonElement

class NLPAPIImpl(deviceManager: DeviceManager) : BaseRest(deviceManager) {

    private val api: NLPAPI by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(NLPAPI::class.java)
    }

    fun query(query: String, attributes: String, callBack: KCallBack<StatusBean<NLPBean>>) {
        api.query(getBaseRequestParams().apply {
            this["query"] = query
            this["attributes"] = attributes
        }).enqueue(callBack)
    }

    fun nlpModel(callBack: KCallBack<StatusBean<NLPModel>>) {
        api.nlpModel(getBaseRequestParams()).enqueue(callBack)
    }

    fun queryPage(page: Int, queryId: String?, callBack: KCallBack<StatusBean<NLPPageBean>>) {
        api.queryPage(getBaseRequestParams().apply {
            this["page"] = "$page"
            this["query_id"] = "$queryId"
        }).enqueue(callBack)
    }

    fun postHistory(cid: String?, movieId: String?, callBack: KCallBack<JsonElement>) {
        api.postHistory(getBaseRequestParams().apply {
            this["movieCid"] = "$cid"
            this["movieId"] = "$movieId"
        }).enqueue(callBack)
    }
}