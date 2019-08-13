package com.fangtang.tv.sdk.base.nlp.tf


/**
 * @author WeiPeng
 * @version 1.0
 * @title TensorFlowProcessor.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/06/28 20:41
 * @changeRecord [修改记录] <br/>
 */

import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.logging.logE
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer

class TensorFlowProcessor(builder: Builder) {

    private val LEN_TOKEN = 15

    private val mTokenMap = HashMap<String, Float>()
    private val mLabelMap = HashMap<Int, String>()

    private val tensorFlow: Interpreter
    private val bufferData: ByteBuffer

    init {
        // 加载Token
        builder.getToken().bufferedReader().use {
            val json = JSONObject(it.readText())
            json.keys().forEach { key ->
                mTokenMap[key] = json.getInt(key).toFloat()
            }
        }

        // 加载Label
        builder.getLabel().bufferedReader().use {
            val json = JSONObject(it.readText())
            json.keys().forEach { key ->
                mLabelMap[json.getInt(key)] = key
            }
        }

        tensorFlow = Interpreter(builder.getModel(), Interpreter.Options().apply { setNumThreads(2) })
        bufferData = ByteBuffer.allocateDirect(LEN_TOKEN * Int.SIZE_BYTES)
        bufferData.order(ByteOrder.nativeOrder())

        logD { "TensorFlow 加载成功" }
    }

    fun run(query: String): String? {
        mapperQuery2TokenBuffer(query)
        val result = Array(1) { FloatArray(mLabelMap.size) }
        tensorFlow.run(bufferData, result)

        logE {
            val sb = StringBuilder("RUN结果：")
            for (index in 0 until result[0].size) {
                sb.append(result[0][index])
                if (index != result[0].size - 1) sb.append(",")
            }
            sb.toString()
        }

        var domainIndex = 2
        var lastNum = 0f
        for (index in 0 until result[0].size) {
            if (result[0][index] > lastNum) {
                lastNum = result[0][index]
                domainIndex = index
                if (lastNum == 1f) break
            }
        }

        val domain = mLabelMap[domainIndex]
        logE { "找到 -> index: $domainIndex  domain:$domain" }
        return domain
    }

    private fun mapperQuery2TokenBuffer(query: String) {
        val tokenArray = query.map { if (mTokenMap.containsKey(it.toString())) mTokenMap[it.toString()] else mTokenMap["<UNK>"] }.let {
            FloatArray(LEN_TOKEN) { 0F }.apply {
                // 后补零
                for (index in 0 until Math.min(it.size, LEN_TOKEN)) this[index] = it[index]!!
                // 前补零
//                for (index in it.size - 1 downTo 0) this[index + (tokenLen - it.size)] = it[index]!!
            }
        }

        logE {
            val sb = StringBuilder("$query 转 Float：")
            for (index in 0 until tokenArray.size) {
                sb.append(tokenArray[index])
                if (index != tokenArray.size - 1) sb.append(",")
            }
            sb.toString()
        }

//        return ByteBuffer.allocate(tokenArray.size * Int.SIZE_BYTES).apply { tokenArray.forEach { putFloat(it) } }
        bufferData.apply {
            rewind()
            tokenArray.forEach { putFloat(it) }
        }
    }

    class Builder {

        private var dataLoader: AbstractDataLoader? = null

        fun setDataLoader(loader: AbstractDataLoader): Builder {
            dataLoader = loader
            return this
        }

        fun build(): TensorFlowProcessor {
            return TensorFlowProcessor(this)
        }

        fun getToken() = dataLoader!!.provideToken()

        fun getLabel() = dataLoader!!.provideLabel()

        fun getModel() = dataLoader!!.provideTensorFlowModel()
    }
}

abstract class AbstractDataLoader {

    abstract fun provideToken(): InputStream
    abstract fun provideLabel(): InputStream
    abstract fun provideTensorFlowModel(): MappedByteBuffer

}
