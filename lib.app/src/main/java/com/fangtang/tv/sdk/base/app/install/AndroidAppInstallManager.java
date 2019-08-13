package com.fangtang.tv.sdk.base.app.install;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.logging.FLog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AndroidAppInstallManager implements IInstallManager {

    private static final String TAG = AndroidAppInstallManager.class.getSimpleName();

    private Map<Object, AppInstallListener> installListenerList
            = new HashMap<>();

    private static AndroidAppInstallManager instance;
    private Context context;
    private Handler mHandler;

    private AndroidAppInstallManager() {
    }

    public synchronized static AndroidAppInstallManager getInstance() {
        if (instance == null) {
            synchronized (AndroidAppInstallManager.class) {
                if (instance == null) {
                    instance = new AndroidAppInstallManager();
                }
            }
        }
        return instance;
    }

    public void init(AppInstallConfiguration configuration) {
        this.context = configuration.context;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public boolean addAppInstallListener(AppInstallListener listener) {
        if (installListenerList != null && !installListenerList.containsKey(listener.tag)) {
            installListenerList.put(listener.tag, listener);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAppInstallListener(AppInstallListener listener) {
        installListenerList.remove(listener.tag);
        return true;
    }

    private void notifyAppInstallSuccess(AppInfoBean appInfoBean) {
        if (installListenerList != null && installListenerList.size() > 0) {
            AppInstallListener listener = installListenerList.get(appInfoBean.md5);
            try {
                if (listener != null) {
                    listener.onAppInstallSuccess(appInfoBean);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAppInstallError(AppInfoBean appInfoBean, Exception ex) {
        if (installListenerList != null && installListenerList.size() > 0) {
            AppInstallListener listener = installListenerList.get(appInfoBean.md5);
            try {
                if (listener != null) {
                    listener.onAppInstallError(appInfoBean, ex);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void installApk(final AppInfoBean appInfoBean) {

        ShellUtils.CommandResult result = ShellUtils.execCmd("chmod 777 " + appInfoBean.path, false);
        if (FLog.isLoggable(FLog.VERBOSE)) {
            FLog.v(TAG, result + "----installApk----action-->>>>" + appInfoBean.path);
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //打开安装dialog
                launchInstallDialog(appInfoBean);

                //监听安装
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
                intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                intentFilter.addDataScheme("package");
                try {
                    InstallReceiver installReceiver = new InstallReceiver(appInfoBean);
                    context.registerReceiver(installReceiver, intentFilter);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void launchInstallDialog(AppInfoBean appInfoBean) {
        File apkFile = new File(appInfoBean.path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()
                    + ".fileProvider", apkFile);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    private class InstallReceiver extends BroadcastReceiver {

        private AppInfoBean appInfoBean;

        public InstallReceiver(AppInfoBean appInfoBean) {
            this.appInfoBean = appInfoBean;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            String action = intent.getAction();

            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "----InstallReceiver----action-->>>>" + action);
            }

            if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                String packageName = null;
                if (intent.getData() != null) {
                    packageName = intent.getData().getSchemeSpecificPart();
                }

                if (!TextUtils.isEmpty(packageName) && packageName.equals(appInfoBean.packageName)) {
                    //callback
                    notifyAppInstallSuccess(appInfoBean);

                    FileUtils.deleteFile(appInfoBean.path);

                    try {
                        context.unregisterReceiver(this);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
