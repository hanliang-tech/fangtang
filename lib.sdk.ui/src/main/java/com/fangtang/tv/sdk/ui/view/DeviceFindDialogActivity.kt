package com.fangtang.tv.sdk.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.blankj.utilcode.util.Utils
import com.fangtang.tv.sdk.Constant
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.extensions.setIntrinsicBoundsDrawable


class DeviceFindDialogActivity : AutoStartLeanCloudActivity(), Runnable, View.OnClickListener {

    companion object {
        @JvmStatic
        fun show(title: String) {
            val intent = Intent(Utils.getApp(), DeviceFindDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("title", title)

            Utils.getApp().startActivity(intent)
        }
    }

    private lateinit var mCodeContainer: ViewGroup
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_device_find_dialog)

        val resId = R.drawable.pic_find_device_ft
        findViewById<TextView>(R.id.tv_device_title).setIntrinsicBoundsDrawable(left = resId)

        mCodeContainer = findViewById(R.id.dialog_code_container)
        title = intent.getStringExtra("title")
        mCodeContainer.postDelayed(this, 500)

    }

    override fun onStart() {
        super.onStart()
        FangTang.getInstance().kvManager.putBoolean(Constant.KEY_SHOW_DEVICE_FIND_DIALOG, true)
    }

    override fun run() {
        findViewById<TextView>(R.id.tv_device_title).text = "发现“${title}”智能音箱"

        findViewById<TextView>(R.id.tv_device_desc).text = "${this.resources.getText(R.string.code_dialog_desc1)}，根据提示，\n念出下方数字即可用音箱控制电视："

        findViewById<View>(R.id.tv_more).setOnClickListener(this)
        findViewById<View>(R.id.tv_close).setOnClickListener(this)

        FangTang.getInstance().remoteManager.getBindDeviceCode(object : IRemoteManager.RemoteDeviceBindCodeListener{
            override fun onBindCodeSuccess(bindCode: RemoteDeviceBindCode?) {
                bindCode?.code?.let { codeStr ->

                    val params = LinearLayout.LayoutParams(DimensUtil.dp2Px(72F), DimensUtil.dp2Px(98F))
                    codeStr.forEachIndexed { index, char ->
                        val tv = View.inflate(this@DeviceFindDialogActivity, R.layout.textview_style_code_dart, null) as TextView
//                    tv.textSize = DimensUtil.dp2Px(69F).toFloat()
                        tv.text = "$char"

                        if (index != 0) {
                            params.leftMargin = DimensUtil.dp2Px(15F)
                        }
                        mCodeContainer.addView(tv, params)
                    }

                }
            }

            override fun onBindCodeError(e: Exception?) {
            }

        })
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tv_more) {
//            val intent = IntentUtils.getLaunchAppIntent("com.fangtang.tv")
//            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//            intent.putExtra(Constant.KEY_FOCUS_INDEX, 1)
//            startActivity(intent)

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fangtang://skip2page/iot"))
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            startActivity(intent)
        }

        finish()
    }

}