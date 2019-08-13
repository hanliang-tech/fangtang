package com.fangtang.tv.sdk.ui.widget

import android.content.Context
import android.graphics.Color
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.fangtang.tv.support.item2.widget.TagWidget


class CustomTagWidget(builder: Builder) : TagWidget(builder) {



    init {
        setScoreTextColor(Color.WHITE)
        setScoreTextSize(DimensUtil.sp2Px(16f))
    }



    override fun onFocusChange(gainFocus: Boolean) {
    }


    class Builder(ctx: Context) : TagWidget.Builder(ctx){
        override fun build(): TagWidget {
            return CustomTagWidget(this)
        }
    }


}