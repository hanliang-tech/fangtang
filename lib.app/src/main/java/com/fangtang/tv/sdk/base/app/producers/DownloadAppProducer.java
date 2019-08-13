package com.fangtang.tv.sdk.base.app.producers;


import android.os.Handler;
import android.os.Looper;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloadManager;
import com.fangtang.tv.sdk.download.core.listener.DownloadListener;
import com.fangtang.tv.sdk.download.view.DownloadViewManager;
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel;

public class DownloadAppProducer implements Producer<AppInfoBean> {

    public static final String TAG = DownloadAppProducer.class.getSimpleName();

    private final Producer<AppInfoBean> mInputProducer;
    private DownloadManager downloadManager;
    private Consumer<AppInfoBean> consumer;
    private AppInfoBean appInfoBean;
    private final DownloadViewManager downloadViewManager;
    private Handler mHandler;

    public DownloadAppProducer(DownloadManager downloadManager,
                               DownloadViewManager downloadViewManager,
                               Producer<AppInfoBean> inputProducer) {
        this.mInputProducer = inputProducer;
        this.downloadViewManager = downloadViewManager;
        this.downloadManager = downloadManager;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void produceResults(Consumer<AppInfoBean> consumer, ProducerContext producerContext) {
        this.consumer = consumer;
        final Consumer<AppInfoBean> c = new DownloadConsumer(consumer);
        mInputProducer.produceResults(c, producerContext);
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadStatusChanged(final Download download) {
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "---1-DownloadAppProducer--onDownloadStatusChanged---->>>>" + download);
            }

            //下载完成
            if (download.getStatus() == Constant.DOWNLOAD_STATE_FINISH) {
                appInfoBean.path = download.getPath();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadViewModel downloadViewModel = new DownloadViewModel();
                        downloadViewModel.id = download.getDownloadId();
                        downloadViewModel.name = appInfoBean.name;
                        downloadViewModel.progress = 1f;
                        downloadViewManager.downloadSuccess(downloadViewModel);
                    }
                });

                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.v(TAG, "--2--DownloadAppProducer--onDownloadStatusChanged---->>>>" + appInfoBean);
                }

                consumer.onNewResult(appInfoBean, Consumer.IS_PARTIAL_RESULT);

                //取消注册监听
                downloadManager.unregisterDownloadListener(downloadListener);
            } else if (download.getStatus() == Constant.DOWNLOAD_STATE_EXCEPTION
                    || download.getStatus() == Constant.DOWNLOAD_STATE_CANCEL
                    || download.getStatus() == Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR
                    || download.getStatus() == Constant.DOWNLOAD_STATE_INIT_ERROR) {
                downloadManager.unregisterDownloadListener(downloadListener);
            }
        }

        @Override
        public void onDownloadListChanged() {
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "----DownloadAppProducer---onDownloadListChanged--->>>>");
            }
        }

        @Override
        public void onProgressUpdate(final Download download) {
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, appInfoBean.md5 + "----onProgressUpdate---->>>>" + download.getDownloadId());
            }
            //update UI
            if (download.getDownloadId().equalsIgnoreCase(appInfoBean.md5)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long progress = -1;
                        try {
                            progress = download.getDownloadSize() * 100 / download.getFileLength();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        DownloadViewModel downloadViewModel = new DownloadViewModel();
                        downloadViewModel.id = download.getDownloadId();
                        downloadViewModel.name = appInfoBean.name;
                        downloadViewModel.progress = progress / 100.0f;
                        downloadViewManager.updateDownloadStatus(downloadViewModel);
                    }
                });
            }
        }
    };

    class DownloadConsumer extends DelegatingConsumer<AppInfoBean, AppInfoBean> {

        private DownloadConsumer(Consumer<AppInfoBean> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final AppInfoBean newResult, int status) {
            if (status == Consumer.IS_DOWNLOAD_DONE) {
                consumer.onNewResult(newResult, status);
                return;
            }

            appInfoBean = newResult;

            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, "----DownloadAppProducer------>>>>" + newResult);
            }

            Download d = downloadManager.getDownload(appInfoBean.md5);
            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, appInfoBean.md5 + "---1-installApp---查询是否在正在下载--->>>>" + appInfoBean);
                FLog.v(TAG, appInfoBean.md5 + "--2--installApp---查询是否在正在下载--->>>>" + d);
            }
            if (d != null) {
                appInfoBean.path = d.getPath();
                if (d.getStatus() == Constant.DOWNLOAD_STATE_DOWNLOADING) {
                    if (FLog.isLoggable(FLog.VERBOSE)) {
                        FLog.v(TAG, appInfoBean.md5 + "----installApp---已经正在下载，无需重新下载--->>>>" + d);
                    }
                    consumer.onNewResult(appInfoBean, Consumer.IS_DOWNLOADING);
                    return;
                }
            }

            if (FLog.isLoggable(FLog.VERBOSE)) {
                FLog.v(TAG, appInfoBean.md5 + "----installApp---没有下载完，继续下载-->>>>");
            }

            downloadListener.tag = newResult.md5;
            downloadManager.registerDownloadListener(downloadListener);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //update UI
                    DownloadViewModel downloadViewModel = new DownloadViewModel();
                    downloadViewModel.id = newResult.md5;
                    downloadViewModel.name = newResult.name;
                    downloadViewModel.progress = 0;
                    downloadViewManager.addDownload(downloadViewModel);
                }
            });

            //download
            Download download = new Download();
            download.setDownloadId(newResult.md5);
            download.setUrl(newResult.url);
            download.setMd5(newResult.md5);
            downloadManager.download(download);
        }
    }
}
