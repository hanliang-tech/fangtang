package com.fangtang.tv.sdk.base.app.producers;


import com.fangtang.tv.sdk.base.app.IAppManager;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;

import java.util.List;
import java.util.concurrent.Executor;

public class FetchInstalledAppListProducer implements Producer<String> {

    public static final String TAG = FetchInstalledAppListProducer.class.getSimpleName();

    private final Producer<List<String>> mInputProducer;
    private final Executor mExecutor;
    private final IAppManager appManager;

    public FetchInstalledAppListProducer(IAppManager appManager, Executor mExecutor, Producer<List<String>> inputProducer) {
        this.mInputProducer = inputProducer;
        this.mExecutor = mExecutor;
        this.appManager = appManager;
    }

    @Override
    public void produceResults(Consumer<String> consumer, ProducerContext producerContext) {
        final Consumer<List<String>> c = new ReportConsumer(consumer);
        mInputProducer.produceResults(c, producerContext);
    }

    class ReportConsumer extends DelegatingConsumer<List<String>, String> {

        private ReportConsumer(Consumer<String> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final List<String> newResult, int status) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (newResult != null && newResult.size() > 0) {
                        StringBuffer sb = new StringBuffer();
                        for (String packageName : newResult) {
                            if (appManager.isAppInstalled(packageName)) {
                                sb.append(packageName);
                                sb.append(",");
                            }
                        }
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        getConsumer().onNewResult(sb.toString(), Consumer.IS_PARTIAL_RESULT);
                    } else {
                        getConsumer().onFailure(new Exception());
                    }
                }
            });
        }
    }
}
