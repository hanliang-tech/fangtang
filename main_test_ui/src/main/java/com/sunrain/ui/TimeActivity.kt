package com.fangtang.tv.sdk

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.fangtang.tv.sdk.ui.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author WeiPeng
 * @version 1.0
 * @title TimeActivity.java
 * @description
 *
 *  need_desc
 *
 * @company 北京奔流网络信息技术有限公司
 * @created  2019/07/25 10:16
 * @changeRecord [修改记录] <br/>
 */

class TimeActivity : BaseActivity() {

    private var tv: TextView? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv = TextView(this)
        tv?.setTextColor(Color.WHITE)
        tv?.setPadding(40, 40, 40, 40)
        tv?.setLineSpacing(10F, 1F)
        tv?.textSize = 24F
        setContentView(tv)

        updateTime()
    }

    private fun updateTime() {

        val c = Calendar.getInstance(TimeZone.getDefault(), Locale.UK)
        c.firstDayOfWeek = Calendar.MONDAY
        c.minimalDaysInFirstWeek = 7

        val sb = StringBuilder()
        sb.append(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.timeInMillis)).append("\n")
        sb.append("星期${parseWeek(c.get(Calendar.DAY_OF_WEEK))}").append("\n")
        sb.append("第${c.get(Calendar.WEEK_OF_YEAR)}周").append("\n")
        sb.append("第${c.get(Calendar.DAY_OF_YEAR)}天").append("\n")

        tv?.text = sb.toString()
        handler.postDelayed({ updateTime() }, 200)
    }

    //    private val weekChinese = arrayListOf("一", "二", "三", "四", "五", "六", "天")
    private val weekChinese = mapOf(
            Calendar.MONDAY to "一",
            Calendar.TUESDAY to "二",
            Calendar.WEDNESDAY to "三",
            Calendar.THURSDAY to "四",
            Calendar.FRIDAY to "五",
            Calendar.SATURDAY to "六",
            Calendar.SUNDAY to "天"
    )

    private fun parseWeek(index: Int): String? {
        return weekChinese[index]
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

}