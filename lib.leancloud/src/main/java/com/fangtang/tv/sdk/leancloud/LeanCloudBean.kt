package com.fangtang.tv.sdk.leancloud

import android.support.annotation.Keep
import com.google.gson.JsonElement

@Keep
data class LeanCloudBean(
        val _lctype: Int,
        val _lctext: String?,
        // 拓展参数
        val _lcattrs: JsonElement?
)