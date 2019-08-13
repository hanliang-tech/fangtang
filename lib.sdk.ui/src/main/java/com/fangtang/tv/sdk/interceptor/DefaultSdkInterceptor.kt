package com.fangtang.tv.sdk.interceptor

import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.fangtang.tv.sdk.base.scanner.ScanDevice

 class DefaultSdkInterceptor : IInterceptor {

    override fun handleDeviceFind(deviceName: ScanDevice): Boolean {
        return false
    }

    override fun handleDeviceBind(): Boolean {
        return false
    }

    override fun handleBeforeQuery(query: String, params: Any?): Boolean {
        return false
    }

    override fun handleQueryResult(nlpBean: NLPBean?): Boolean {
        return false
    }

    override fun handlePageResult(result: NLPPageBean?): Boolean {
        return false
    }

    override fun handleBeforeDownload(): Boolean {
        return false
    }

    override fun handleBeforeInstall(): Boolean {
        return false
    }
}