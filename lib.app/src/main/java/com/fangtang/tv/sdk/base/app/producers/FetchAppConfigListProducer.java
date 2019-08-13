package com.fangtang.tv.sdk.base.app.producers;


import android.support.annotation.Nullable;

import com.fangtang.tv.sdk.base.app.server.AppInfoAPIImpl;
import com.fangtang.tv.sdk.base.bean.StatusBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.net.KCallBack;

import java.util.List;

public class FetchAppConfigListProducer implements Producer<List<String>> {

    public static final String TAG = FetchAppConfigListProducer.class.getSimpleName();

    private AppInfoAPIImpl apkInfoAPI;

    public FetchAppConfigListProducer(DeviceManager deviceManager) {
        apkInfoAPI = new AppInfoAPIImpl(deviceManager);
    }

    @Override
    public void produceResults(Consumer<List<String>> consumer, ProducerContext producerContext) {
        getAppListConfig(consumer);
    }

    private void getAppListConfig(final Consumer<List<String>> consumer) {
        apkInfoAPI.checkAppList(new KCallBack<StatusBean<List<String>>>() {
            @Override
            public void onSuccess(@Nullable StatusBean<List<String>> statusBean) {
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.e(TAG, "---getAppListConfig----->>>>>" + statusBean);
                }
                if (statusBean != null && statusBean.data != null) {
                    consumer.onNewResult(statusBean.data, Consumer.IS_PARTIAL_RESULT);
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
