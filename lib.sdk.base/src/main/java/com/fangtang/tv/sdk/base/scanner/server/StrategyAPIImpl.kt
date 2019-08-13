package com.fangtang.tv.sdk.base.scanner.server

import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.net.KCallBack
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


/**
 * @author WeiPeng
 * @version 1.0
 * @title StrategyAPIImpl.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/07/23 15:09
 * @changeRecord [修改记录] <br/>
 */
class StrategyAPIImpl(deviceManager: DeviceManager) :BaseRest(deviceManager) {

    private val api: IApi by lazy(LazyThreadSafetyMode.NONE) {
        createRetrofitService(IApi::class.java)
    }

    fun reqScanStrategy(callBack: KCallBack<ScanStrategyEntity?>) {
        api.reqScanStrategy(getBaseRequestParams()).enqueue(callBack)
    }

}

interface IApi{
    @POST("/sdk/v1/config/scanpolicy")
    @FormUrlEncoded
    fun reqScanStrategy(@FieldMap params: Map<String, String>): Call<ScanStrategyEntity?>
}