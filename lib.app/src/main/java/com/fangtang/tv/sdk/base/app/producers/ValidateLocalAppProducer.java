package com.fangtang.tv.sdk.base.app.producers;


import android.text.TextUtils;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.util.MD5Utils;
import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloadManager;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.cache.naming.FileNameGenerator;

import java.io.File;
import java.util.concurrent.Executor;

public class ValidateLocalAppProducer implements Producer<AppInfoBean> {

    public static final String TAG = ValidateLocalAppProducer.class.getSimpleName();
    private final Producer<AppInfoBean> mInputProducer;
    private final Executor mExecutor;
    private DownloadManager downloadManager;

    public ValidateLocalAppProducer(DownloadManager downloadManager, Executor executor, Producer<AppInfoBean> inputProducer) {
        this.mInputProducer = inputProducer;
        this.mExecutor = executor;
        this.downloadManager = downloadManager;
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
        protected void onNewResultImpl(final AppInfoBean newResult, final int status) {
            FLog.v(TAG, "----ValidateLocalAppProducer------>>>>" + newResult);
            //如果md5为空,直接返回
            if (TextUtils.isEmpty(newResult.md5)) {
                getConsumer().onNewResult(newResult, status);
            }

            DownloaderConfiguration configuration = downloadManager.getConfiguration();
            if (configuration == null) {
                getConsumer().onNewResult(newResult, status);
            }

            Download downloadTaskBean = downloadManager.getDownload(newResult.md5);
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, newResult.md5 + "---1-ValidateDownloadAppConsumer---查询是否在正在下载--->>>>" + newResult);
                FLog.v(TAG, newResult.md5 + "--2--ValidateDownloadAppConsumer---查询是否在正在下载--->>>>" + downloadTaskBean);
            }

            //应用正在下载
            if (downloadTaskBean != null && downloadTaskBean.getStatus() == Constant.DOWNLOAD_STATE_DOWNLOADING) {
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.v(TAG, newResult.md5 + "---1-ValidateDownloadAppConsumer---应用正在下载--->>>>" + newResult);
                }
                getConsumer().onNewResult(newResult, Consumer.IS_DOWNLOADING);
                return;
            }

            FileNameGenerator nameGenerator = configuration.diskCacheFileNameGenerator;
            Download d = new Download();
            d.setDownloadId(newResult.md5);
            String fileName = nameGenerator.generate(d);

            final File downloadFile = new File(configuration.cacheDir, fileName);
            FLog.v(TAG, "----ValidateLocalAppProducer--文件目录---->>>>" + downloadFile.getAbsolutePath());
            //文件存在，验证md5值
            if (downloadFile.exists() && downloadFile.isFile()) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String md5String = null;
                        try {
                            md5String = MD5Utils.getFileMD5String(downloadFile);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        FLog.v(TAG, "----ValidateLocalAppProducer--生成文件的MD5值---->>>>" + md5String);
                        if (!TextUtils.isEmpty(md5String) && newResult.md5.equalsIgnoreCase(md5String)) {

                            if (FLog.isLoggable(FLog.VERBOSE)) {
                                FLog.v(TAG, "----ValidateLocalAppProducer--MD5验证通过---->>>>" + md5String);
                            }
                            //
                            newResult.path = downloadFile.getAbsolutePath();

                            getConsumer().onNewResult(newResult, Consumer.IS_DOWNLOAD_DONE);
                        }
                        //md5 not equals
                        else {
                            try {
                                downloadFile.delete();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            if (FLog.isLoggable(FLog.VERBOSE)) {
                                FLog.v(TAG, "----ValidateLocalAppProducer--MD5验证失败---->>>>");
                            }
                            getConsumer().onNewResult(newResult, Consumer.IS_PARTIAL_RESULT);
                        }
                    }
                });
            }
            //文件不存在，直接返回
            else {
                FLog.v(TAG, "----ValidateLocalAppProducer--文件不存在---->>>>" + newResult);
                getConsumer().onNewResult(newResult, status);
            }
        }
    }
}
