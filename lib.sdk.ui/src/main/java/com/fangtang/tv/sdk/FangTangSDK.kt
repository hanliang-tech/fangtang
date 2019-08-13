package com.fangtang.tv.sdk

import android.app.Application
import android.text.TextUtils
import com.fangtang.tv.sdk.base.app.AppConfiguration
import com.fangtang.tv.sdk.base.app.AppManager
import com.fangtang.tv.sdk.base.logging.LOG_TAG
import com.fangtang.tv.sdk.base.nlp.bean.NLPBean
import com.fangtang.tv.sdk.base.push.LeanCloudPushHandler
import com.fangtang.tv.sdk.base.scanner.IScannerManager
import com.fangtang.tv.sdk.base.scanner.ScanDevice
import com.fangtang.tv.sdk.interceptor.DefaultSdkInterceptor
import com.fangtang.tv.sdk.interceptor.IInterceptor
import com.fangtang.tv.sdk.ui.utils.CommonUtils
import com.fangtang.tv.sdk.ui.utils.NlpBehaviour
import com.fangtang.tv.sdk.ui.view.DeviceFindDialogActivity
import com.fangtang.tv.support.item2.utils.DimensUtil


class FangTangSDK {

    private lateinit var sdkInterceptor: DefaultSdkInterceptor

    companion object {
        @Volatile
        private var tInstance: FangTangSDK? = null

        @JvmStatic
        fun get(): FangTangSDK {
            if (tInstance == null) {
                synchronized(FangTangSDK::class.java) {
                    if (tInstance == null) {
                        tInstance = FangTangSDK()
                    }
                }
            }
            return tInstance!!
        }

        //TODO
        private const val LEAN_CLOUD_APP_ID = ""
        private const val LEAN_CLOUD_CLIENT_KEY = ""
    }

    fun init(context: Application, channel: String) {
        init(context, DefaultSdkInterceptor(), channel)
    }

    private fun init(context: Application, interceptor: DefaultSdkInterceptor, channel: String) {

        LOG_TAG = "[-fangtangsdk-]"

        initBase(context)
        initSDK(context, channel)
        sdkInterceptor = interceptor
    }

    private fun initBase(context: Application) {
        DimensUtil.init(context)
    }

    private fun initSDK(context: Application, channel: String) {
        val fangTang = FangTang.getInstance()
        val appManager = AppManager.getInstance()
        val configuration = FangTangConfiguration.Builder(context)
//                .setAppId("your app_id")
//                .setAppKey("your app_secret")
                .setChannel(channel)
                .setPushAppKey(LEAN_CLOUD_APP_ID)
                .setPushClientKey(LEAN_CLOUD_CLIENT_KEY)
                .setAppManager(appManager)
                .build()

        val appConfiguration = AppConfiguration.Builder(context)
                .setExecutorSupplier(configuration.mExecutorSupplier)
                .setDeviceManager(configuration.deviceManager)
                .reportInstalledApp(true)
                .build()
        appManager.init(appConfiguration)

        fangTang.init(configuration)

        // 注册push消息
        fangTang.pushManager.addReceivePushMessageListener(LeanCloudPushHandler())

        FangTang.getInstance().scannerManager.addDeviceScannerListener(object : IScannerManager.DeviceScannerListener {
            override fun onScanDevice(device: ScanDevice) {
                FangTang.getInstance().scannerManager.removeDeviceScannerListener(this)

                if (!sdkInterceptor.handleDeviceFind(device)) {
                    if (TextUtils.isEmpty(device.deviceName)) return
                    // 检测开关
                    if (CommonUtils.isFindDeviceSwitchOn()) {
                        DeviceFindDialogActivity.show(device.deviceName)
                    }
                }
            }
        })

        FangTang.getInstance().scannerManager.startScan()
    }

    fun getSdkInterceptor(): IInterceptor {
        return sdkInterceptor
    }

    fun query(query: String) {
        query(query, null)
    }

    fun query(query: String, callBack: IQueryCallBack?) {
        if (!sdkInterceptor.handleBeforeQuery(query, null)) {
            NlpBehaviour.handleQuery(query, null, callBack)
        }
    }

    interface IQueryCallBack {
        fun onQuery(bean: NLPBean)
    }
}