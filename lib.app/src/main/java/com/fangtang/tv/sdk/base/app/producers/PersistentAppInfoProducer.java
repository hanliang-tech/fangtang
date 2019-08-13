package com.fangtang.tv.sdk.base.app.producers;


import android.content.Context;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.app.db.APKDao;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.logging.FLog;

import java.util.List;
import java.util.concurrent.Executor;

public class PersistentAppInfoProducer implements Producer<List<AppInfoBean>> {

    public static final String TAG = PersistentAppInfoProducer.class.getSimpleName();

    private final Executor mExecutor;
    private final Producer<List<AppInfoBean>> mInputProducer;
    private APKDao apkDao;

    public PersistentAppInfoProducer(Context context, Executor executor, Producer<List<AppInfoBean>> inputProducer) {
        mExecutor = executor;
        mInputProducer = inputProducer;
        apkDao = new APKDao(context);
    }

    @Override
    public void produceResults(Consumer<List<AppInfoBean>> consumer, ProducerContext producerContext) {
        final Consumer<List<AppInfoBean>> persistentConsumer = new PersistentConsumer(consumer);
        mInputProducer.produceResults(persistentConsumer, producerContext);
    }

    class PersistentConsumer extends DelegatingConsumer<List<AppInfoBean>, List<AppInfoBean>> {

        private PersistentConsumer(Consumer<List<AppInfoBean>> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final List<AppInfoBean> newResult, int status) {

            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (newResult != null && newResult.size() > 0) {
                        for (AppInfoBean dataBean : newResult) {
                            FLog.e(TAG, "--------InstallAppConsumer--------->>>>>" + Thread.currentThread());
                            apkDao.insert(dataBean);
                        }
                    }

                    getConsumer().onNewResult(newResult, Consumer.IS_LAST);
                }
            });
        }
    }
}
