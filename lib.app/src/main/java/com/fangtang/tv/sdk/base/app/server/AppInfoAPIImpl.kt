package com.fangtang.tv.sdk.base.app.server

import com.fangtang.tv.sdk.base.app.AppInfoListBean
import com.fangtang.tv.sdk.base.bean.ResultBean
import com.fangtang.tv.sdk.base.bean.StatusBean
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.BaseRest
import retrofit2.Callback

class AppInfoAPIImpl(deviceManager: DeviceManager) : BaseRest(deviceManager) {

    private val api: AppInfoAPI by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(AppInfoAPI::class.java)
    }

    fun getApkList(callback: Callback<StatusBean<AppInfoListBean>>) {
        api.getApkList(getBaseRequestParams()).enqueue(callback)
    }

    fun reportInstalledApkList(packageList: String, callback: Callback<StatusBean<ResultBean>>) {
        api.reportInstalledApkList(getBaseRequestParams().apply {
            this["package"] = packageList
        }).enqueue(callback)
    }

    fun checkAppList(callback: Callback<StatusBean<List<String>>>) {
        api.checkAppList(getBaseRequestParams()).enqueue(callback)
    }
}