package com.fangtang.tv.sdk.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import com.fangtang.tv.baseui.widget.FTextView


class BindDeviceTextView : FTextView {

    private var defaultBorderColor = 0x2BFFFFFF
    private var defaultFillColor = 0x0DFFFFFF
    private var defaultTextColor = Color.WHITE

    private var focusTextColor = Color.BLACK
    private var focusFillColor = Color.WHITE

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)

//    init {
//        setBackgroundDrawable(context.resources.getDrawable(R.drawable.device_bg_name))
//        setPadding(DimensUtil.dp2Px(25F), DimensUtil.dp2Px(10F), DimensUtil.dp2Px(25F), DimensUtil.dp2Px(10F))
//    }

    fun setDefaultBorderColor(color: Int) {
        defaultBorderColor = color
        applyViewColor(false)
    }

    fun setDefaultTextColor(color: Int) {
        defaultTextColor = color
        applyViewColor(false)
    }


    override fun onFocusChanged(hasFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(hasFocus, direction, previouslyFocusedRect)

        applyViewColor(hasFocus)
    }

    private fun applyViewColor(hasFocus: Boolean) {
        this.setTextColor(if (hasFocus) focusTextColor else defaultTextColor)

        val bgDrawable = ((this.background as LayerDrawable).getDrawable(0) as GradientDrawable)
        bgDrawable.setStroke(2, if (hasFocus) Color.TRANSPARENT else defaultBorderColor)
        bgDrawable.setColor(if (hasFocus) focusFillColor else defaultFillColor)
    }

}