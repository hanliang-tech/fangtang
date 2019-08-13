package com.fangtang.tv.sdk.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.Utils
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindStatus
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.BaseDialogActivity


class UnbindDeviceDialogActivity : BaseDialogActivity(), View.OnClickListener {

    companion object {
        @JvmStatic
        fun show(id: String) {
            val intent = Intent(Utils.getApp(), UnbindDeviceDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id", id)

            Utils.getApp().startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_unbind_dialog)

        findViewById<View>(R.id.tv_ok).setOnClickListener(this)
        findViewById<View>(R.id.tv_close).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tv_ok) {
            val id = intent.getStringExtra("id")
            unbindDevice(id)
        }
        dismiss()
    }

    private fun unbindDevice(id: String) {
        logD { "解除绑定:$id" }

        FangTang.getInstance().remoteManager.unbindDeviceInfo(id, object : IRemoteManager.RemoteDeviceUnBindListener {
            override fun onUnBindDeviceSuccess(status: RemoteDeviceBindStatus?) {
            }

            override fun onUnBindDeviceError(e: Exception?) {
            }

        })
    }

}