package com.fangtang.tv.sdk.ui.view

import android.content.Context
import android.view.LayoutInflater
import com.fangtang.tv.support.item2.host.FrameLayoutHostView


class CommonLayoutHostView(ctx: Context, layoutId: Int, hasFocus: Boolean = true) : FrameLayoutHostView(ctx) {

    init {
        LayoutInflater.from(ctx).inflate(layoutId, this, true)
        setFocusable(hasFocus)
    }

}