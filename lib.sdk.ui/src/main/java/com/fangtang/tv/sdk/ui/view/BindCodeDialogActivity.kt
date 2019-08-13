package com.fangtang.tv.sdk.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.blankj.utilcode.util.Utils
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode
import com.fangtang.tv.sdk.ui.R


class BindCodeDialogActivity : AutoStartLeanCloudActivity(), Runnable {

    companion object {
        @JvmStatic
        fun show() {
            val intent = Intent(Utils.getApp(), BindCodeDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            Utils.getApp().startActivity(intent)
        }
    }

    private lateinit var mCodeContainer: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_code_dialog)

        mCodeContainer = findViewById(R.id.dialog_code_container)
        mCodeContainer.postDelayed(this, 500)
    }

    override fun run() {
        FangTang.getInstance().remoteManager.getBindDeviceCode(object :IRemoteManager.RemoteDeviceBindCodeListener{
            override fun onBindCodeSuccess(bindCode: RemoteDeviceBindCode?) {
                bindCode?.code?.let { codeStr ->

                    val params = LinearLayout.LayoutParams(DimensUtil.dp2Px(67F), DimensUtil.dp2Px(91F))
                    codeStr.forEachIndexed { index, char ->
                        val tv = View.inflate(this@BindCodeDialogActivity, R.layout.textview_style_code_dart, null) as TextView
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
}