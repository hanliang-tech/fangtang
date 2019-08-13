package com.fangtang.tv.sdk.base.nlp.tf

import android.os.AsyncTask
import okhttp3.*
import okio.*

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * FileDownloader
 * Created by Eminem on 2016/3/26.
 */
class OkHttpDownloadUtil {

    private var downLoaderAsyncTask: DownLoaderAsyncTask? = null
    private var downloadProgressListener: DownloadProgressListener? = null

    fun downLoadFile(url: String, storePath: String, downloadProgressListener: DownloadProgressListener) {
        downLoaderAsyncTask = DownLoaderAsyncTask(url, storePath)
        downLoaderAsyncTask!!.execute(url)
        this.downloadProgressListener = downloadProgressListener
    }

    fun cancel() {
        downLoaderAsyncTask?.cancel(true)
    }

    private inner class DownLoaderAsyncTask(private val url: String, private val storePath: String) : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String): String? {

            val request = Request.Builder()
                    .url(url)
                    .build()

            val progressListener = object : ProgressListener {
                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {

                    downloadProgressListener!!.onProgressUpdate(
                            (100 * bytesRead / contentLength).toInt(), bytesRead, contentLength)
                }
            }

            val client = OkHttpClient.Builder()
                    .addNetworkInterceptor { chain ->
                        val originalResponse = chain.proceed(chain.request())
                        originalResponse.newBuilder()
                                .body(ProgressResponseBody(originalResponse.body()!!, progressListener))
                                .build()
                    }
                    .connectTimeout(30, TimeUnit.MINUTES)
                    .writeTimeout(30, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.MINUTES)
                    .build()


            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                response!!.body()!!.source().readAll(Okio.sink(File(storePath)))

                if (!response.isSuccessful) {
                    downloadProgressListener!!.onError(Exception("Unexpected code $response"))
                } else {
                    downloadProgressListener!!.onFinish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                downloadProgressListener!!.onError(e)
            }


            return null
        }


        override fun onCancelled() {
            super.onCancelled()
            downloadProgressListener!!.onCancel()
        }

    }


    interface DownloadProgressListener {
        fun onProgressUpdate(progress: Int, bytesRead: Long, contentLength: Long)
        fun onFinish()
        fun onCancel()
        fun onError(e: Exception)
    }

    inner class ProgressResponseBody(private val responseBody: ResponseBody, private val progressListener: ProgressListener) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()))
            }
            return bufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                internal var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }

    interface ProgressListener {
        fun update(bytesRead: Long, contentLength: Long, done: Boolean)
    }
}
