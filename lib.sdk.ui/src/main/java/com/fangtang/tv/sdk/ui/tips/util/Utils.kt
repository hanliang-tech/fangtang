package com.fangtang.tv.sdk.ui.tips.util

import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.TypedValue

object Utils {

    const val PCK_VOICE_HELP = "com.fangtang.tv.voicehelp"

    private var mDebug = false

    @JvmStatic
    fun setDebug(debug: Boolean) {
        mDebug = debug
    }

    fun dp2px(context: Context, dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

//    private fun getBeanFromCache(context: Context, fileName: String, currentTime: Long) =
//            json2Bean(readeCache(context, fileName).takeIf { currentTime < it.first && it.second.isNotEmpty() }?.second)
//
//    private fun getBeanFromInternet(context: Context, url: String, map: HashMap<String, String>) =
//            getJsonFromInternet(context, url, map).let { json2Bean(it) }

//    fun getJsonFromInternet(context: Context, url: String, map: HashMap<String, String>): String {
//
//        if (isNetWorkAvailable(context)) {
////            Log.e("Less","tips load net")
//            var conn: HttpURLConnection? = null
//            var out: OutputStream? = null
//            var reader: InputStreamReader? = null
//            try {
//
//                var args = ""
//                map.forEach {
//                    args = args.plus("${it.key}=${it.value}&")
//                }
//
//                conn = URL(url).openConnection() as HttpURLConnection
//                conn.requestMethod = "POST"
//                conn.connectTimeout = 10000
//                conn.readTimeout = 10000
//                conn.useCaches = false
//                conn.setRequestProperty("Keep-Alive", "false")
//                conn.setRequestProperty("Connection", "close")
//                conn.doOutput = true
//                conn.doInput = true
//                out = conn.outputStream
//                out.write(args.toByteArray(Charsets.UTF_8))
//                out.flush()
//
//                if (conn.responseCode == 200) {
//                    reader = conn.inputStream.buffered(512).reader()
//                    return reader.readText()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                reader?.close()
//                out?.close()
//                conn?.disconnect()
//            }
//
//        }
//
//        return "{}"
//    }

    //region 缓存相关

    private fun write2File(context: Context, fileName: String, content: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE)
                    .bufferedWriter()
                    .use { out ->
                        out.write(content)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    private fun readeCache(context: Context, fileName: String): Pair<Long, String> {
//        var outDateTime = 0L
//        var content = ""
//        try {
//            context.openFileInput(fileName)
//                    .bufferedReader()
//                    .use(BufferedReader::readLines).forEachIndexed { index, line ->
//                        if (index == 0) {
//                            outDateTime = line.toLong()
//                        } else {
//                            content = content.plus(line)
//                        }
//                    }
//        } catch (e: Exception) {
//            logD("can not read cache file.")
//        }
//        return Pair(outDateTime, content)
//    }


    //endregion

//    private fun json2Bean(json: String?): Bean? {
//        var bean: Bean? = null
//
//        if (json.isNullOrEmpty()) {
//            return bean
//        }
//
//        try {
//            val root = JSONObject(json)
//            val dataJo = root.getJSONObject("data")
//            val tipJa = dataJo.getJSONArray("tips")
//
//            val list = (0 until tipJa.length())
//                    .map { tipJa.getJSONObject(it) }
//                    .map {
//                        VoiceBarEntity.Tip(
//                                it.getString("key"),
//                                it.getString("value"),
//                                it.getInt("type"),
//                                it.getString("color")
//                        )
//                    }
//                    .toMutableList()
//
//            val data = Data(
//                    dataJo.getString("view_id"),
//                    dataJo.getString("apk_bundle"),
//                    dataJo.getString("apk_version"),
//                    if (dataJo.has("word_count")) dataJo.getInt("word_count") else -1,
//                    list,
//                    dataJo.getInt("code"),
//                    dataJo.getInt("expire")
//            )
//
//            bean = Bean(
//                    root.getInt("code"),
//                    root.getString("info"),
//                    data
//            )
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return bean
//    }

    private fun isNetWorkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        } else {
            val cManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (cManager == null) {
                return false
            } else {
                try {
                    val info = cManager.activeNetworkInfo
                    if (info != null && info.isAvailable) {
                        return true
                    }
                } catch (var3: Exception) {
                    var3.printStackTrace()
                }

                return false
            }
        }
    }

    fun getAppVersionWithPackage(ctx: Context, pck: String): String {
        try {
            if (!TextUtils.isEmpty(pck)) {
                val appInfo =
                        ctx.packageManager.getPackageInfo(pck, 0)
                if (appInfo != null) {
                    return appInfo.versionName
                }
            }
        } catch (e: Exception) {
        }
        return ""
    }

    private var lastTime = 0L

    fun checkLastTime(): Boolean {
        return checkLastTime(500)
    }

    fun checkLastTime(millsTime: Long): Boolean {
        val time = System.currentTimeMillis()
        if (time - lastTime > millsTime) {
            lastTime = time
            return true
        }
        return false
    }

}

//fun <E> List<E>.shuffle() = Collections.shuffle(this)