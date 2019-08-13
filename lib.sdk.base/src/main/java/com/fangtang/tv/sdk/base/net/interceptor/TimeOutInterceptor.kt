package com.fangtang.tv.sdk.base.net.interceptor

import com.fangtang.tv.sdk.base.net.BaseRest
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


class TimeOutInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain
                .run { request.header(BaseRest.CONNECT_TIMEOUT)?.let { chain.withConnectTimeout(it.toInt(), TimeUnit.MILLISECONDS) } ?: chain }
                .run { request.header(BaseRest.READ_TIMEOUT)?.let { chain.withReadTimeout(it.toInt(), TimeUnit.MILLISECONDS) } ?: chain }
                .run { request.header(BaseRest.WRITE_TIMEOUT)?.let { chain.withWriteTimeout(it.toInt(), TimeUnit.MILLISECONDS) } ?: chain }
                .proceed(request)
    }
}