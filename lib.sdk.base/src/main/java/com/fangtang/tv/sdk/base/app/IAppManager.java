package com.fangtang.tv.sdk.base.app;

public interface IAppManager {

    boolean isAppInstalled(AppInfoBean appInfoBean);

    boolean isAppInstalled(String packageName);

    void installApp(AppInfoBean appInfoBean, InstallAppListener listener);

    interface InstallAppListener {
        void onInstallAppSuccess(AppInfoBean appInfoBean);

        void onInstallAppError(AppInfoBean appInfoBean, Throwable e);
    }
}
