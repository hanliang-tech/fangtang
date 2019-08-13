package com.fangtang.tv.sdk.base.app.producers;


import android.support.annotation.Nullable;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.app.AppInfoListBean;
import com.fangtang.tv.sdk.base.app.server.AppInfoAPIImpl;
import com.fangtang.tv.sdk.base.bean.StatusBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.net.KCallBack;

import java.util.List;

public class FetchAppInfoListProducer implements Producer<List<AppInfoBean>> {

    public static final String TAG = FetchAppInfoListProducer.class.getSimpleName();

    private AppInfoAPIImpl apkInfoAPI;

    public FetchAppInfoListProducer(DeviceManager deviceManager) {
        this.apkInfoAPI = new AppInfoAPIImpl(deviceManager);
    }

    @Override
    public void produceResults(Consumer<List<AppInfoBean>> consumer, ProducerContext producerContext) {
        getAppList(consumer);
    }

    private void getAppList(final Consumer<List<AppInfoBean>> consumer) {
        apkInfoAPI.getApkList(new KCallBack<StatusBean<AppInfoListBean>>() {

            @Override
            public void onSuccess(@Nullable StatusBean<AppInfoListBean> statusBean) {
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.e(TAG, "---getApkList----->>>>>" + statusBean);
                }
                if (statusBean != null && statusBean.data != null && statusBean.data.list != null) {
                    consumer.onNewResult(statusBean.data.list, Consumer.IS_PARTIAL_RESULT);
                } else {
                    consumer.onFailure(new Exception());
                }
            }

            @Override
            public void onFail(int code, @Nullable String msg) {
                consumer.onFailure(new Exception(msg));
            }
        });
    }
}
