package com.fangtang.tv.sdk.ui.extensions

import android.support.annotation.DrawableRes
import android.widget.TextView


fun TextView.setIntrinsicBoundsDrawable(
        @DrawableRes left: Int = 0,
        @DrawableRes top: Int = 0,
        @DrawableRes right: Int = 0,
        @DrawableRes bottom: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

