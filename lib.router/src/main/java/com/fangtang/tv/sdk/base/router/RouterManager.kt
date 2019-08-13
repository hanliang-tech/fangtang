package com.fangtang.tv.sdk.base.router

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.fangtang.tv.sdk.base.app.AppInfoBean
import com.fangtang.tv.sdk.base.app.IAppManager
import com.fangtang.tv.sdk.base.logging.FLog

class RouterManager(private val appManager: IAppManager) : IRouterManager {

    companion object {
        private const val TAG = "RouterManager"
    }

    private lateinit var application: Application

    override fun init(application: Application) {
        this.application = application
    }

    override fun launchIntent(json: String) {
        val intentBean = AppIntentDispatcher.parserIntent(json)
        if (TextUtils.isEmpty(intentBean.packageName)) {
            //TODO
            return
        }

        //construct app info bean
        val appInfoBean = AppInfoBean()
        appInfoBean.packageName = intentBean.packageName
        appInfoBean.versionCode = intentBean.versioncode
        appInfoBean.name = intentBean.name
        appInfoBean.md5 = intentBean.md5
        appInfoBean.url = intentBean.uri

        //installed
        if (appManager.isAppInstalled(appInfoBean)) {
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "------RouterManager app is already installed---->>>>>" + intentBean.intentToast)
            }
            try {
                if (!TextUtils.isEmpty(intentBean.intentToast)) {
                    ToastUtils.showShort(intentBean.intentToast)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            AppIntentDispatcher.jump(application, intentBean, intent)

        }
        //install
        else {
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "------RouterManager app not installed---->>>>>")
            }
            appManager.installApp(appInfoBean, null)
        }
    }

//    // 如果已经安装
//    if (callBack.checkAppValidate(jsonBean.packageName, jsonBean.versioncode))
//    {
//        // 跳转
//        val intent = Intent()
//        try {
//            callBack.beforeJump(intent, jsonBean.packageName, jsonBean.intentToast)
//            jump(context, jsonBean, intent)
//        } catch (e: Throwable) {
//            e.printStackTrace()
//            callBack.jumpError(json, e.message)
//        }
//
//    } else
//    {
//        callBack.doDownload(jsonBean.name, jsonBean.packageName, jsonBean.versioncode, jsonBean.downloadUrl, jsonBean.md5)
//    }
//}

//    fun start(context: Context, json: String) {
//        logW { "跳转Json:$json" }
//        AppIntentDispatcher.dispatch(context, json, object : AppIntentDispatcher.DispatcherCallBack {
//            override fun jsonParseError(msg: String, json: String?) {
//                logE { "json parse error: msg:$msg json:$json" }
//            }
//
//            override fun checkAppValidate(pck: String, versionCode: String?): Boolean {
//                logD { "checkAppValidate pck:$pck ver:$versionCode" }
//                AppUtils.getAppInfo(pck)?.let {
//                    if (!TextUtils.isEmpty(versionCode)) {
//                        if (versionCode!!.toInt() <= it.versionCode) {
//                            return true
//                        }
//                    }
//                    return true
//                }
//                return false
//            }
//
//            override fun doDownload(name: String?, pck: String?, versioncode: String?, downloadUrl: String?, md5: String?) {
//                if (TextUtils.isEmpty(downloadUrl)) {
//                    SupportMessageManager.run(MessageData(LOAD_APK, pck!!))
//                } else {
//                    logD { "走下载逻辑...." }
//                    val job = AppHubJob()
//                    job.addJob(name, name, pck, "-1", md5, downloadUrl, true)
//                    download(job)
//                    return
//                }
//            }
//
//            override fun beforeJump(intent: Intent, pck: String, toast: String) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//                if (!checkSpecial(pck)) {
//                    logD { "add FLAG_ACTIVITY_CLEAR_TASK" }
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                }
//                ToastUtils.showShort(toast)
//            }
//
//            override fun jumpError(json: String?, message: String?) {
//                logE { "Jump err. msg:$message json:$json" }
//            }
//        })
//    }

//    fun download(job: AppHubJob) {
//        // 干预下载
//        if (!MarketUtil.canDownload()) {
//            // 视久的跳转其应用时长
//            if (MarketUtil.isShiJiu()) {
//                ShiJiuUtils.openShiJiuAppStore(job.getPackageName(0))
//            }
//            ToastUtils.showLong("未安装 ${job.jobName}\n到应用商店安装后即正常使用")
//            return
//        }
//        SupportMessageManager.run(MessageData(TYPE_DOWNLOAD, job))
//
//        // 排除METV
//        if (!checkSpecial(job.getPackageName(0)) && KV.get("first_download", true) && !TextUtils.isEmpty(VoiceStatus.downloadTipImg)) {
//            KV.put("first_download", false)
//            EventBus.getDefault().post(EventInstall(0))
//        }
//    }
//
//    private fun checkSpecial(json: String): Boolean {
//        return json.contains("qclive.tv")
//    }

    override fun release() {

    }
}

