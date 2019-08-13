package com.fangtang.tv.sdk.base.app.producers;


import android.text.TextUtils;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.util.MD5Utils;

import java.io.File;
import java.util.concurrent.Executor;

public class ValidateDownloadAppProducer implements Producer<AppInfoBean> {

    public static final String TAG = ValidateDownloadAppProducer.class.getSimpleName();
    private final Producer<AppInfoBean> mInputProducer;
    private final Executor mExecutor;

    public ValidateDownloadAppProducer(Executor executor, Producer<AppInfoBean> inputProducer) {
        this.mInputProducer = inputProducer;
        this.mExecutor = executor;
    }

    @Override
    public void produceResults(Consumer<AppInfoBean> consumer, ProducerContext producerContext) {
        final Consumer<AppInfoBean> c = new ValidateDownloadAppConsumer(consumer);
        mInputProducer.produceResults(c, producerContext);
    }

    class ValidateDownloadAppConsumer extends DelegatingConsumer<AppInfoBean, AppInfoBean> {

        private ValidateDownloadAppConsumer(Consumer<AppInfoBean> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final AppInfoBean newResult, int status) {
            FLog.v(TAG, status + "--status--ValidateDownloadAppProducer--1---->>>>" + newResult);
            if (status == Consumer.IS_DOWNLOADING) {
                getConsumer().onNewResult(newResult, status);
                return;
            }

            if (TextUtils.isEmpty(newResult.path)) {
                getConsumer().onFailure(new Exception("download path is null..."));
                return;
            }
            FLog.v(TAG, "----ValidateDownloadAppProducer--2---->>>>" + newResult.path);
            final File downloadFile = new File(newResult.path);
            FLog.v(TAG, downloadFile.isFile() + "----ValidateDownloadAppProducer--2---->>>>" + (downloadFile.exists()));
            if (!downloadFile.exists() || !downloadFile.isFile()) {
                FLog.v(TAG, "----ValidateDownloadAppProducer--文件不存在---->>>>" + newResult.path);
                getConsumer().onFailure(new Exception("download file is not exists..."));
                return;
            }

            if (!TextUtils.isEmpty(newResult.md5)) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String md5String = null;
                        try {
                            md5String = MD5Utils.getFileMD5String(downloadFile);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        FLog.v(TAG, "----ValidateDownloadAppProducer--生成文件的MD5值---->>>>" + md5String);
                        if (!TextUtils.isEmpty(md5String)
                                && newResult.md5.equalsIgnoreCase(md5String)) {

                            if (FLog.isLoggable(FLog.VERBOSE)) {
                                FLog.v(TAG, "----ValidateDownloadAppProducer--MD5验证通过---->>>>" + md5String);
                            }

                            getConsumer().onNewResult(newResult, Consumer.IS_PARTIAL_RESULT);
                        } else {
                            if (FLog.isLoggable(FLog.VERBOSE)) {
                                FLog.v(TAG, "----ValidateDownloadAppProducer--MD5验证失败---->>>>");
                            }
                            getConsumer().onFailure(new Exception("md5 check invalidate..."));
                        }
                    }
                });
            } else {
                getConsumer().onNewResult(newResult, Consumer.IS_PARTIAL_RESULT);
            }
        }
    }
}
