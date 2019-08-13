package com.fangtang.tv.gson
import com.google.gson.Gson
import java.lang.ref.SoftReference

object GsonManager {

    private var gsonRef: SoftReference<Gson>? = null

    @JvmStatic
    fun getGson(): Gson {
        var gson: Gson? = gsonRef?.get()
        if (gson == null) {
            gson = Gson()
            gsonRef = null
            gsonRef = SoftReference(gson)
        }
        return gson
    }
}