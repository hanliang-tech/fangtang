package com.fangtang.tv.sdk.base.scanner

import com.fangtang.tv.sdk.base.kv.IKVManager
import com.fangtang.tv.sdk.base.logging.logD
import java.util.*


/**
 * @author WeiPeng
 * @version 1.0
 * @title ScanStrategy.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/07/17 19:11
 * @changeRecord [修改记录] <br/>
 */
class ScanStrategy(
        private val key: String,
        private val field: Int,
        private val kv: IKVManager,
        private val calendar: Calendar,
        private val max: Int) {

    var first = 0
    var second = 0

    init {
        val data = kv.getString(key, "").let {
            val data = it.split(",")
            if (data.size > 1) intArrayOf(data[0].toInt(), data[1].toInt()) else intArrayOf(calendar.get(field), 0)
        }
        first = data[0]
        second = data[1]
    }

    /**
     * 是否符合规则 [x,y] (y < strategy.value)
     */
    fun pass(): Boolean {
        logD { "$key [$first,$second] max:$max" }
        return calendar.get(field) != first || second < max
    }

    /**
     * 更新弹窗次数
     */
    fun update() {
        if (calendar.get(field) != first) {
            first = calendar.get(field)
            second = 0
        }
        second++
        kv.putString(key, "$first,$second")
        logD { "更新：$key 值：$first 次数：$second 限制：$max" }
    }

}