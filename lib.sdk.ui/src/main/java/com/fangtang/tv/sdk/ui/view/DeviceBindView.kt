package com.fangtang.tv.sdk.ui.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.fangtang.tv.baseui.widget.FRelativeLayout
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.bumptech.glide.Glide
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.remote.bean.RemoteDevice
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.utils.CommonUtils


class DeviceBindView : FRelativeLayout {

    private var mTvTitle: TextView? = null
    private var mDynamicContainer: LinearLayout? = null
    private var mInfoImage: ImageView? = null
//    private var mVersionText: TextView? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(ctx, attrs, defStyleAttr, defStyleRes)

    @SuppressLint("SetTextI18n")
    override fun onFinishInflate() {
        super.onFinishInflate()

        mTvTitle = findViewById(R.id.tv_device_title)
        mDynamicContainer = findViewById(R.id.dynamic_child_container)
        mInfoImage = findViewById(R.id.iv_device_info)
//        mVersionText = findViewById(R.id.tv_version)


//        mVersionText?.text = "v${AppUtils.getAppVersionName()}"
    }

    fun setTitle(title: String) {
        mTvTitle?.text = title
    }

    fun setCode(code: String) {
        mDynamicContainer?.removeAllViews()
        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftMargin = DimensUtil.dp2Px(12F)
        code.forEach { char ->
            val tv = View.inflate(context, R.layout.textview_style_code, null) as TextView
            tv.text = "$char"
            mDynamicContainer?.addView(tv, params)
        }
    }

    fun setDevices(devices: MutableList<RemoteDevice>, bindButtonClick: (deviceId: String) -> Unit = {}) {
        mDynamicContainer?.removeAllViews()

//        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//        params.leftMargin = DimensUtil.dp2Px(12F)
//        devices.forEach {
//            val tv = getDeviceNameTextView(it)
//            mDynamicContainer?.addView(tv, params)
//        }
//
//        // 如果已经弹窗过了 显示一个控制弹窗的按钮
//        val KV = FangTang.getInstance().kvManager
//        if (KV.getBoolean(Constant.KEY_SHOW_DEVICE_FIND_DIALOG, false)) {
//
//            val isOpen = CommonUtils.isFindDeviceSwitchOn()
//            val tvIotSearchSwitch = View.inflate(context, R.layout.textview_style_name, null) as TextView
//            tvIotSearchSwitch.isFocusable = true
//            updateSwitchText(tvIotSearchSwitch, isOpen)
//
//            tvIotSearchSwitch.setOnClickListener {
//                val targetSwitch = !CommonUtils.isFindDeviceSwitchOn()
//                updateSwitchText(tvIotSearchSwitch, targetSwitch)
//                KV.putBoolean(Constant.KEY_IOT_SEARCH_SWITCH, targetSwitch)
//            }
//
//            mDynamicContainer?.addView(tvIotSearchSwitch, params)
//        }

        val params = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.leftMargin = DimensUtil.dp2Px(12F)

        val tmpTextColor = Color.parseColor("#6EFF7F")
        devices.forEach { d ->
            val tv = View.inflate(context, R.layout.textview_style_name, null) as BindDeviceTextView
            tv.isFocusable = true
            tv.setDefaultBorderColor(tmpTextColor)
            tv.setDefaultTextColor(tmpTextColor)
            tv.text = d.name
            tv.tag = d.device_id
            tv.setOnClickListener { bindButtonClick(it.tag as String) }
            mDynamicContainer?.addView(tv, params)
        }

        val tv = View.inflate(context, R.layout.textview_style_name, null) as BindDeviceTextView
        tv.isFocusable = true
        tv.setDefaultBorderColor(0x2BFFFFFF)
        tv.text = "+ 添加音箱"
        tv.setOnClickListener { bindButtonClick("add") }
        mDynamicContainer?.addView(tv, params)

        // 如果已经弹窗过了 显示一个控制弹窗的按钮
        val KV = FangTang.getInstance().kvManager
        if (KV.getBoolean(Constant.KEY_SHOW_DEVICE_FIND_DIALOG, false)) {

            val isOpen = CommonUtils.isFindDeviceSwitchOn()
            val tvIotSearchSwitch = View.inflate(context, R.layout.textview_style_name, null) as TextView
            tvIotSearchSwitch.isFocusable = true
            updateSwitchText(tvIotSearchSwitch, isOpen)

            tvIotSearchSwitch.setOnClickListener {
                val targetSwitch = !CommonUtils.isFindDeviceSwitchOn()
                updateSwitchText(tvIotSearchSwitch, targetSwitch)
                KV.putBoolean(Constant.KEY_IOT_SEARCH_SWITCH, targetSwitch)
            }

            mDynamicContainer?.addView(tvIotSearchSwitch, params)
        }

        mDynamicContainer?.getChildAt(0)?.requestFocus()
    }

    private fun updateSwitchText(tv: TextView, isOpen: Boolean) {
        tv.text = "发现音箱：${if (isOpen) "开" else "关"}"
    }

    fun setImage(url: String?) {
        Glide.with(context).load(url).into(mInfoImage)
    }

}