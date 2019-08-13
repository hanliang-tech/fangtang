package com.fangtang.tv.sdk.ui.presenter

import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import com.blankj.utilcode.util.Utils
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.base.push.IPushManager
import com.fangtang.tv.sdk.base.push.PushMessage
import com.fangtang.tv.sdk.base.push.PushMsgType
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.base.BaseContract
import com.fangtang.tv.sdk.ui.view.BindCodeDialogActivity
import com.fangtang.tv.sdk.ui.view.DeviceBindView
import com.fangtang.tv.sdk.ui.view.UnbindDeviceDialogActivity


class IotFragmentPresenter(private val iView: IView) : BaseContract.Presenter(), IPushManager.ReceivePushMessageListener {

    private var needRefresh = false

    private val handler = Handler(Looper.getMainLooper())

    private val REFRESH_TIME = DateUtils.MINUTE_IN_MILLIS * 10

    override fun onResume() {
        super.onResume()
        if (needRefresh) {
            needRefresh = false
            iView.findView<DeviceBindView>(R.id.device_bind_view)?.postDelayed({ requestData() }, 1000)
        }
    }

    override fun onStart() {
        super.onStart()
        requestData()
    }

    override fun onCreate() {
        super.onCreate()
        FangTang.getInstance().pushManager.addReceivePushMessageListener(this)

        handler.postDelayed({FangTang.getInstance().pushManager.open(null)}, DateUtils.SECOND_IN_MILLIS * 8)
        handler.postDelayed({loopRefreshData()}, REFRESH_TIME)
    }

    private fun loopRefreshData(){
        handler.postDelayed({loopRefreshData()}, REFRESH_TIME)
    }

    override fun onReceivePushMessage(pushMessage: PushMessage) {
        when (pushMessage.messageType) {
            PushMsgType.PUSH_IOT_BIND,
            PushMsgType.PUSH_IOT_UNBIND -> requestData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FangTang.getInstance().pushManager.removeReceivePushMessageListener(this)

        handler.removeCallbacksAndMessages(null)
    }

    private fun requestData() {
        // 逻辑
        // 1、请求绑定接口
        // 2、根据设备数量显示不同界面
        FangTang.getInstance().remoteManager.getBindDeviceInfo(object : IRemoteManager.RemoteDeviceBindInfoListener {
            override fun onBindInfoSuccess(bindInfo: RemoteDeviceBindInfo) {
                fillView(bindInfo)
            }

            override fun onBindInfoError(e: Exception?) {
            }
        })

    }

    private fun fillView(info: RemoteDeviceBindInfo) {
        // 渲染数据会让焦点飞走 先将焦点设置到当前的tab
        // 不在这个分类不处理
        //                    if (needFixFocus) {
        //                    (activity as MiddleActivity?)?.getPresenter()?.requestTabFocus()
        //                    }

        val bindView = iView.findView<DeviceBindView>(R.id.device_bind_view)

        // 有设备
        // 显示设备列表
        if (info.isBindIotDevice) {
            fillWithDevice(bindView, info)
        }
        // 没有设备
        // 显示绑定code
        else {
            fillWithNoBindDevice(bindView, info)
        }
    }

    private fun fillWithNoBindDevice(bindView: DeviceBindView?, info: RemoteDeviceBindInfo) {
        FangTang.getInstance().remoteManager.getBindDeviceCode(object : IRemoteManager.RemoteDeviceBindCodeListener {
            override fun onBindCodeSuccess(bindCode: RemoteDeviceBindCode?) {
                if (bindCode != null) {
                    bindView?.setTitle(Utils.getApp().resources.getString(R.string.bind_desc))
                    bindView?.setCode(bindCode.code ?: "0")
                    bindView?.setImage(info.un_connected_image)
                }
            }

            override fun onBindCodeError(e: Exception?) {
            }

        })
    }

    private fun fillWithDevice(bindView: DeviceBindView?, info: RemoteDeviceBindInfo): Unit? {
        bindView?.setTitle("当前已经连接的音箱：")
        bindView?.setDevices(info.devices) { id ->
            if (id == "add") {
                BindCodeDialogActivity.show()
            } else {
                needRefresh = true
                UnbindDeviceDialogActivity.show(id)
            }
        }
        return bindView?.setImage(info.connected_image)
    }

    interface IView : BaseContract.IView
}