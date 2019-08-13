package com.fangtang.tv.sdk.ui.model

import android.support.annotation.Keep
import android.text.TextUtils
import android.view.View
import com.fangtang.tv.waterfall.GridDataFactory
import com.fangtang.tv.waterfall.ItemModel
import com.fangtang.tv.support.item2.presenter.CommonItemPresenter
import com.fangtang.tv.support.item2.widget.TagWidget

@Keep
open class WaterfallModel(type: String, width: Float, height: Float, private var span: Int = 1) : ItemModel(type), CommonItemPresenter.IModel, GridDataFactory.Item {

    var realIndex = 0       // 真实index
    var displayIndex = 0    // 显示index
    var coverUrl = ""       // 封面URL
    var name = ""           // Title
    var grade = ""          // 评分
    var customData = ""     // 跳转方式

    var cid: String? = null       // 视频分类ID
    var movieId: String? = null   // 视频ID
    var tag: String? = null      // 右上角角标

    // 第三方角标的名称
    var originName = ""
    var originIconUrl = ""
    var packageName:String? = null

    // 本地图片
    var localDrawableRes = 0

    init {
        this.width = width
        this.height = height
    }

    override fun getSpan(): Int {
        return span
    }

    fun setSpan(s:Int){
        span = s
    }

    override fun getRawModel(): ItemModel {
        return this
    }

    // ======================
    override fun getNumberScaleOffset(): Float {
        return 1F
    }

    override fun getCover(): Any {
        return coverUrl
    }

    override fun getNumIndex(): Int {
        return displayIndex
    }

    override fun getItemNumViewShow(): Int {
        return View.VISIBLE
    }

    // ======================
    override fun getIconType(): String {
        return tag ?: TagWidget.CORNER_NONE
    }

    override fun getTitle(): String {
        return name
    }

    override fun getScore(): String {
        return if(TextUtils.isEmpty(grade)) "7.0" else grade
    }

}