package com.fangtang.tv.sdk.base.app.install;


import com.fangtang.tv.sdk.base.app.AppInfoBean;

public interface IInstallManager {

    void installApk(AppInfoBean appInfoBean);

    abstract class AppInstallListener implements AppBaseInstallListener {
        public Object tag;
    }

    boolean addAppInstallListener(AppInstallListener listener);

    boolean removeAppInstallListener(AppInstallListener listener);

}
