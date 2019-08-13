package com.fangtang.tv.sdk.base.app.producers;


import android.os.Handler;
import android.os.Looper;

import com.fangtang.tv.sdk.base.app.AppInfoBean;
import com.fangtang.tv.sdk.base.app.install.IInstallManager;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.DelegatingConsumer;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.core.ProducerContext;
import com.fangtang.tv.sdk.download.view.DownloadViewManager;
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel;

public class InstallAppProducer implements Producer<AppInfoBean> {

    public static final String TAG = InstallAppProducer.class.getSimpleName();
    private final Producer<AppInfoBean> mInputProducer;
    private final IInstallManager appInstallManager;
    private final DownloadViewManager downloadViewManager;

    private Consumer<AppInfoBean> consumer;
    private AppInfoBean installAppInfo;
    private Handler mHandler;

    public InstallAppProducer(Producer<AppInfoBean> inputProducer,
                              DownloadViewManager downloadViewManager,
                              IInstallManager appInstallManager) {
        this.mInputProducer = inputProducer;
        this.appInstallManager = appInstallManager;
        this.downloadViewManager = downloadViewManager;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void produceResults(Consumer<AppInfoBean> consumer, ProducerContext producerContext) {
        this.consumer = consumer;
        final Consumer<AppInfoBean> persistentConsumer = new InstallAppConsumer(consumer);
        mInputProducer.produceResults(persistentConsumer, producerContext);
    }


    private IInstallManager.AppInstallListener installListener = new IInstallManager.AppInstallListener() {

        @Override
        public void onAppInstallSuccess(AppInfoBean appInfoBean) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //update UI
                    DownloadViewModel downloadViewModel = new DownloadViewModel();
                    downloadViewModel.id = installAppInfo.md5;
                    downloadViewModel.name = installAppInfo.name;
                    downloadViewManager.installSuccess(downloadViewModel);
                }
            });
            appInstallManager.removeAppInstallListener(installListener);
            consumer.onNewResult(installAppInfo, Consumer.IS_LAST);
        }

        @Override
        public void onAppInstallError(AppInfoBean appInfoBean, Exception ex) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //update UI
                    DownloadViewModel downloadViewModel = new DownloadViewModel();
                    downloadViewModel.id = installAppInfo.md5;
                    downloadViewModel.name = installAppInfo.name;
                    downloadViewManager.installError(downloadViewModel);
                }
            });

            appInstallManager.removeAppInstallListener(installListener);
            consumer.onFailure(new Exception("install app error..."));
        }
    };

    class InstallAppConsumer extends DelegatingConsumer<AppInfoBean, AppInfoBean> {

        private InstallAppConsumer(Consumer<AppInfoBean> consumer) {
            super(consumer);
        }

        @Override
        protected void onNewResultImpl(final AppInfoBean newResult, int status) {
            if (status == Consumer.IS_DOWNLOADING) {
                getConsumer().onNewResult(newResult, status);
                return;
            }

            installAppInfo = newResult;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //update UI
                    DownloadViewModel downloadViewModel = new DownloadViewModel();
                    downloadViewModel.id = newResult.md5;
                    downloadViewModel.name = newResult.name;
                    downloadViewManager.installApp(downloadViewModel);

                    installListener.tag = newResult.md5;
                    appInstallManager.addAppInstallListener(installListener);
                    //invoke install manager
                    appInstallManager.installApk(newResult);
                }
            });
        }
    }
}
