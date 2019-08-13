package com.fangtang.tv.sdk.base.remote.server.impl

import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.net.KCallBack
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindStatus
import com.fangtang.tv.sdk.base.remote.server.RemoteAPI

class RemoteAPIImpl(deviceManager: DeviceManager) : BaseRest(deviceManager) {

    private val api: RemoteAPI by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(RemoteAPI::class.java)
    }

    fun getBindDeviceInfo(callBack: KCallBack<StatusBean<RemoteDeviceBindInfo>>) {
        api.bindDeviceInfo(getBaseRequestParams()).enqueue(callBack)
    }


    fun bindDeviceCode(callBack: KCallBack<StatusBean<RemoteDeviceBindCode>>) {
        api.bindDeviceCode(getBaseRequestParams()).enqueue(callBack)
    }

    fun unbindDeviceInfo(device_id: String, callBack: KCallBack<StatusBean<RemoteDeviceBindStatus>>) {
        api.unbindDevice(getBaseRequestParams().apply {
            this["device_id"] = device_id
        }).enqueue(callBack)
    }
}