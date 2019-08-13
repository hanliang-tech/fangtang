package com.fangtang.tv.sdk.base.net

import com.fangtang.tv.sdk.base.Version
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.net.ServerParams.APP_ID
import com.fangtang.tv.sdk.base.net.ServerParams.APP_VERSION
import com.fangtang.tv.sdk.base.net.ServerParams.CHANNEL_CODE
import com.fangtang.tv.sdk.base.net.ServerParams.PACKAGE_NAME
import com.fangtang.tv.sdk.base.net.ServerParams.PREVIEW
import com.fangtang.tv.sdk.base.net.ServerParams.SDK_VERSION
import com.fangtang.tv.sdk.base.net.ServerParams.TIME
import com.fangtang.tv.sdk.base.net.ServerParams.UUID
import com.fangtang.tv.sdk.base.net.interceptor.GzipRequestInterceptor
import com.fangtang.tv.sdk.base.net.interceptor.KInterceptor
import com.fangtang.tv.sdk.base.net.interceptor.TimeOutInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


open class BaseRest(private val deviceManager: DeviceManager) {

    companion object {
        const val SDK_RELEASE = "http://sdk.fangtangtv.com"
        const val SDK_DEBUG = "http://test-sdk.fangtangtv.com"
        const val HOST = "@HOST"
        const val CONNECT_TIMEOUT = "@CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "@READ_TIMEOUT"
        const val WRITE_TIMEOUT = "@WRITE_TIMEOUT"
        var logEnable = true
        var isPreview = false
    }

    private val okHttpClient: OkHttpClient = createOkHttpClientConfiguration()
    private var restAdapter: Retrofit = createRetrofitConfiguration()

    fun <T> createRetrofitService(clazz: Class<T>): T = restAdapter.create(clazz)

    private fun createRetrofitConfiguration(): Retrofit = Retrofit.Builder()
            .baseUrl(getBaseRequestUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    private fun createOkHttpClientConfiguration(): OkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addNetworkInterceptor(getLogInterceptor())
            .addInterceptor(KInterceptor(getSecretKey()))
            .addInterceptor(TimeOutInterceptor())
            .addInterceptor(GzipRequestInterceptor())
            .build()

    private fun getLogInterceptor(): Interceptor = HttpLoggingInterceptor()
            .apply {
                level = if (logEnable) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            }

    open fun getBaseRequestUrl(): String {
        return SDK_RELEASE
    }

    open fun getSecretKey() = deviceManager.appKey

    open fun getBaseRequestParams(): MutableMap<String, String> {
        return mutableMapOf(
                APP_ID to deviceManager.appId,
                CHANNEL_CODE to deviceManager.channel,
                UUID to deviceManager.uuid,
                APP_VERSION to "${deviceManager.appVersionCode}",
                SDK_VERSION to Version.VERSION,
                PACKAGE_NAME to deviceManager.packageName,
                TIME to System.currentTimeMillis().toString(),
                PREVIEW to if(isPreview) "preview" else ""

        )
    }
}