package com.fangtang.tv.sdk.download.view.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView


class DownloadTextView : TextView {

    private val borderColor = Color.parseColor("#ccffffff")
    private val blueColor = Color.parseColor("#2a8dff")
    private val backgroundColor = Color.parseColor("#202426")

    private var mWith: Int = 0
    private var mHeight: Int = 0
    private var mRadius = 0f
    private val mBorderWith = dp2px(context!!, 1.3f)
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    //0.0-1.0
    var mProcess: Float = 0.0f


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun onUpdate(p: Float) {
        mProcess = p
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWith = w
        mHeight = h
        mRadius = mHeight / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND

        mPaint.color = borderColor
        mPaint.strokeWidth = mHeight.toFloat()
        canvas?.drawLine(mRadius, mHeight / 2f, mWith.toFloat() - mRadius, mHeight / 2f, mPaint)

        mPaint.strokeWidth = mHeight.toFloat() - 2 * mBorderWith
        mPaint.color = backgroundColor
        canvas?.drawLine(mRadius + mBorderWith * 0.5f, mHeight / 2f, mWith.toFloat() - mRadius - mBorderWith * 0.5f, mHeight / 2f, mPaint)

        mPaint.color = blueColor
        mPaint.strokeWidth = mHeight.toFloat()
        canvas?.drawLine(mRadius, mHeight / 2f, mRadius + mProcess * (mWith - 2 * mRadius), mHeight / 2f, mPaint)

        super.onDraw(canvas)
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}