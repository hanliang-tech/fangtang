package com.fangtang.tv.sdk.interceptor

import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.fangtang.tv.sdk.base.scanner.ScanDevice

interface IInterceptor {

    /**
     * 发现设备
     */
    fun handleDeviceFind(deviceName: ScanDevice): Boolean

    /**
     * 设备绑定成功
     */
    fun handleDeviceBind(): Boolean

    /**
     * 获取到query
     */
    fun handleBeforeQuery(query: String, params: Any?): Boolean

    /**
     * Nlp请求的结果
     */
    fun handleQueryResult(nlpBean: NLPBean?): Boolean

    /**
     * Nlp分页结果
     */
    fun handlePageResult(result: NLPPageBean?): Boolean

    /**
     * 下载之前调用
     */
    fun handleBeforeDownload(): Boolean

    /**
     * 安装之前调用
     */
    fun handleBeforeInstall(): Boolean

}