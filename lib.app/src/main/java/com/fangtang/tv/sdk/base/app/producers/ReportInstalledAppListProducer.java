package com.fangtang.tv.sdk.base.app.producers;

import android.support.annotation.Nullable;

import com.fangtang.tv.sdk.base.app.server.AppInfoAPIImpl;
import com.fangtang.tv.sdk.base.bean.ResultBean;
import com.fangtang.tv.sdk.base.bean.StatusBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.net.KCallBack;

public class ReportInstalledAppListProducer implements Producer<Boolean> {

    public static final String TAG = ReportInstalledAppListProducer.class.getSimpleName();

    private AppInfoAPIImpl apkInfoAPI;
    private final Producer<String> mInputProducer;

    public ReportInstalledAppListProducer(DeviceManager deviceManager, Producer<String> inputProducer) {
        this.mInputProducer = inputProducer;
        this.apkInfoAPI = new AppInfoAPIImpl(deviceManager);
    }

    @Override
    public void produceResults(Consumer<Boolean> consumer, ProducerContext producerContext) {
        final Consumer<String> c = new ReportConsumer(consumer);
        mInputProducer.produceResults(c, producerContext);
    }

    class ReportConsumer extends DelegatingConsumer<String, Boolean> {

        private ReportConsumer(Consumer<Boolean> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final String newResult, int status) {
            apkInfoAPI.reportInstalledApkList(newResult, new KCallBack<StatusBean<ResultBean>>() {
                @Override
                public void onSuccess(@Nullable StatusBean<ResultBean> statusBean) {
                    if (FLog.isLoggable(FLog.VERBOSE)) {
                        FLog.e(TAG, "---reportInstalledApkList----->>>>>" + statusBean);
                    }
                    if (statusBean != null && statusBean.data != null && statusBean.isSuccess()) {
                        getConsumer().onNewResult(statusBean.isSuccess(), Consumer.IS_LAST);
                    } else {
                        getConsumer().onFailure(new Exception());
                    }
                }

                @Override
                public void onFail(int code, @Nullable String msg) {
                    getConsumer().onFailure(new Exception(msg));
                }
            });
        }
    }
}
