package com.fangtang.tv.sdk.base.net.interceptor

import android.text.TextUtils
import com.fangtang.tv.sdk.base.net.BaseRest
import com.fangtang.tv.sdk.base.util.SignUtil
import okhttp3.*
import okhttp3.MultipartBody.FORM
import java.net.URL


open class KInterceptor(private val appSecret:String?) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain!!.request()
        val httpUrl = request.url()
        val builder = request.newBuilder()
        val dynamicHost = request.header(BaseRest.HOST)
        val okHttpUrl: HttpUrl
        val url = httpUrl.url()
        okHttpUrl = if (!TextUtils.isEmpty(dynamicHost)) {
            builder.removeHeader(BaseRest.HOST)
            httpUrl.newBuilder().host(dynamicHost).build()
        } else {
            httpUrl.newBuilder().build()
        }
        return response(okHttpUrl, url, request, builder, chain)
    }

    private fun response(okHttpUrl: HttpUrl, url: URL, request: Request, builder: Request.Builder, chain: Interceptor.Chain): Response {
        val method = url.path.replace("/", "")
        val host = okHttpUrl.host()
        val bodyBuilder = FormBody.Builder()
        if (TextUtils.isEmpty(host)) {
            if (!TextUtils.isEmpty(method)) {
                bodyBuilder.add("method", method)
                val newUrl = okHttpUrl.newBuilder().removePathSegment(0).toString()
                val ok = builder.url(newUrl).method(request.method(), bodyBuilder.build()).build()
                return chain.proceed(ok)
            }
        } else {
            return proceed(builder, okHttpUrl, request, chain, method)
        }
        return chain.proceed(builder.url(okHttpUrl).build())
    }


    private fun commonParams(bodyBuilder: FormBody.Builder, method: String) {
    }

    private fun hasVersion(bodyBuilder: FormBody.Builder): Boolean {
        val formBody = bodyBuilder.build()
        if (formBody == null || formBody.size() == 0) {
            return false
        }
        val size = formBody.size()
        for (i in 0 until size) {
            if (formBody.encodedName(i) == "version") {
                return true
            }
        }
        return false
    }


    private fun proceed(builder: Request.Builder, okHttpUrl: HttpUrl, request: Request, chain: Interceptor.Chain, method: String): Response {
        val bodyBuilder = FormBody.Builder()
        var ok = Request.Builder()
        if (request.body() != null) {
            if (request.body() is FormBody) {
                val fromBody = (request.body() as FormBody)
                for (i in 0..(fromBody.size() - 1)) {
                    bodyBuilder.add(fromBody.name(i), fromBody.value(i))
                }
                commonParams(bodyBuilder, method)
                ok = builder.url(okHttpUrl).method(request.method(), getFormBody(bodyBuilder))
            } else if (request.body() is MultipartBody) {
                val requestBody = MultipartBody.Builder().setType(FORM)
                val oldFormBody = request.body() as MultipartBody
                for (i in 0 until oldFormBody.size()) {
                    requestBody.addPart(oldFormBody.part(i))
                }
                ok = builder.url(okHttpUrl).post(requestBody.build())
            } else {
                commonParams(bodyBuilder, method)
                ok = builder.url(okHttpUrl).method(request.method(), getFormBody(bodyBuilder))
            }
        }
        return chain.proceed(ok.build())
    }

    private fun getFormBody(builder: FormBody.Builder): FormBody {
        val data = builder.build()
        val map = mutableMapOf<String, String>()
        for (i in 0..(data.size() - 1)) {
            map[data.name(i)] = data.value(i)
        }
        val sign = SignUtil.getSign(map, appSecret)
        builder.add("sign", sign)
        return builder.build()
    }

}