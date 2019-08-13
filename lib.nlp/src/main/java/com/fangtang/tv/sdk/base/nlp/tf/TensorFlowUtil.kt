package com.fangtang.tv.sdk.base.nlp.tf

import android.text.TextUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import com.blankj.utilcode.util.ZipUtils
import com.fangtang.tv.sdk.base.logging.logD
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


/**
 * @author WeiPeng
 * @version 1.0
 * @title TensorFlowUtil.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/06/28 20:45
 * @changeRecord [修改记录] <br/>
 */
object TensorFlowUtil {

    private var processor: TensorFlowProcessor? = null

    @JvmStatic
    fun update(versionCode: String?, url: String?) {

        if (TextUtils.isEmpty(versionCode) || TextUtils.isEmpty(url)) return

        MainScope().launch {

            logD { "加载TensorFlow version:$versionCode  url:$url" }

            val basePath = PathUtils.getInternalAppFilesPath() + "/.tf"
            val zipPath = "$basePath/$versionCode.zip"
            val unZipPath = "$basePath/$versionCode/"

            // 先根据VersionCode读取解压文件 读取失败则下载
            var tfMap = loadTF(zipPath, unZipPath)
            if (tfMap.isNullOrEmpty()) {
                FileUtils.deleteDir(basePath)
                FileUtils.createOrExistsDir(basePath)
                logD { "清除旧版本" }

                OkHttpDownloadUtil().downLoadFile(url!!, zipPath, object : OkHttpDownloadUtil.DownloadProgressListener {
                    override fun onProgressUpdate(progress: Int, bytesRead: Long, contentLength: Long) {
                    }

                    override fun onFinish() {
                        logD { "下载成功" }

                        tfMap = loadTF(zipPath, unZipPath)
                        processor = TensorFlowProcessor.Builder()
                                .setDataLoader(SdcardDataLoader(tfMap)).build()

                    }

                    override fun onCancel() {
                    }

                    override fun onError(e: Exception) {
                        logD { "下载失败 $e" }
                        processor = TensorFlowProcessor.Builder()
                                .setDataLoader(AssetsDataLoader()).build()
                    }

                })

            } else {
                processor = TensorFlowProcessor.Builder()
                        .setDataLoader(SdcardDataLoader(tfMap)).build()
            }
        }
    }

    @JvmStatic
    fun process(query: String): String? {
        return processor?.run(query)
    }

    private fun loadTF(zipPath: String, unZipPath: String): Map<String, File>? {

        try {
            val listFiles = FileUtils.listFilesInDir(unZipPath)
            if (listFiles == null || listFiles.size == 0) {
                logD { "没有找到版本对应文件 尝试重新解压" }
                ZipUtils.unzipFile(zipPath, unZipPath)
                return loadTF(zipPath, unZipPath)
            }

            val map = HashMap<String, File>(3)

            listFiles.forEach { file ->
                when {
                    file.name.contains("token") -> map["token"] = file
                    file.name.contains("label") -> map["label"] = file
                    file.name.contains("model") -> map["model"] = file
                }
            }
            logD { "文件读取成功" }
            return map
        } catch (e: Exception) {
            logD { "加载异常 ${e.message}" }
        }
        return null
    }

    private class AssetsDataLoader : AbstractDataLoader() {

        init {
            logD { "使用Assets文件" }
        }

        override fun provideToken(): InputStream {
            return Utils.getApp().assets.open("tensorflow/tokens.json")
        }

        override fun provideLabel(): InputStream {
            return Utils.getApp().assets.open("tensorflow/labels.json")
        }

        override fun provideTensorFlowModel(): MappedByteBuffer {
            // 加载Model
            val fileDescriptor = Utils.getApp().assets.openFd("tensorflow/model.tflite")
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength

            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    private class SdcardDataLoader(private val map: Map<String, File>?) : AbstractDataLoader() {

        init {
            logD { "使用更新文件" }
        }

        override fun provideToken(): InputStream {
            return FileInputStream(map!!["token"])
        }

        override fun provideLabel(): InputStream {
            return FileInputStream(map!!["label"])
        }

        override fun provideTensorFlowModel(): MappedByteBuffer {
            RandomAccessFile(map!!["model"], "r").use {
                return it.channel.map(FileChannel.MapMode.READ_ONLY, 0, it.length())
            }
        }
    }

}

