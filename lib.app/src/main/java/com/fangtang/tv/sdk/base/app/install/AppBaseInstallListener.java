package com.fangtang.tv.sdk.base.app.install;

import com.fangtang.tv.sdk.base.app.AppInfoBean;


public interface AppBaseInstallListener {
    void onAppInstallSuccess(AppInfoBean appInfoBean);

    void onAppInstallError(AppInfoBean appInfoBean, Exception ex);
}
