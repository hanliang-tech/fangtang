package com.fangtang.tv.sdk.base.app.producers;


import android.content.Context;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.app.db.APKDBHelper;
import com.fangtang.tv.sdk.base.app.db.APKDao;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.logging.FLog;

import java.util.List;
import java.util.concurrent.Executor;

public class LocalFetchAppInfoProducer implements Producer<AppInfoBean> {

    public static final String TAG = LocalFetchAppInfoProducer.class.getSimpleName();

    private final Executor mExecutor;
    private APKDao apkDao;
    private AppInfoBean appInfo;

    public LocalFetchAppInfoProducer(Context context, Executor executor, AppInfoBean appInfo) {
        this.mExecutor = executor;
        this.apkDao = new APKDao(context);
        this.appInfo = appInfo;
    }

    @Override
    public void produceResults(Consumer<AppInfoBean> consumer, ProducerContext producerContext) {
        getAppInfo(consumer);
    }

    private void getAppInfo(final Consumer<AppInfoBean> consumer) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<AppInfoBean> appInfoBeanList = apkDao.query(APKDBHelper.COLUMN_PCK_VALUE, appInfo.packageName);

                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.v(TAG, "----getAppInfo------>>>>" + appInfoBeanList);
                }

                if (appInfoBeanList != null && appInfoBeanList.size() > 0 && appInfoBeanList.get(0) != null) {
                    AppInfoBean appInfoBean = appInfoBeanList.get(0);
                    appInfo.versionCode = appInfoBean.versionCode;
                    appInfo.md5 = appInfoBean.md5;
                    appInfo.url = appInfoBean.url;
                    appInfo.name = appInfoBean.name;
                    appInfo.icon = appInfoBean.icon;
                    appInfo.packageName = appInfoBean.packageName;

                    consumer.onNewResult(appInfoBean, Consumer.IS_PARTIAL_RESULT);
                } else {
                    consumer.onFailure(new Exception());
                }
            }
        });
    }
}
