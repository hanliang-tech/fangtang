package com.fangtang.tv.sdk.leancloud.server.impl

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.leancloud.server.LeanCloudAPI
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.base.push.PushConfigBean

class LeanCloudAPIImpl(deviceManager: DeviceManager) : BaseRest(deviceManager) {

    private val fangTangAPI: LeanCloudAPI by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(LeanCloudAPI::class.java)
    }

    fun getLeanCloudConfig(callBack: KCallBack<StatusBean<PushConfigBean>>) {
        fangTangAPI.getLeanCloudConfig(getBaseRequestParams()).enqueue(callBack)
    }
}