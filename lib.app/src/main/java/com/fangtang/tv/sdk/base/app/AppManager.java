package com.fangtang.tv.sdk.base.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.fangtang.tv.sdk.base.app.install.IInstallManager;
import com.fangtang.tv.sdk.base.app.producers.DownloadAppProducer;
import com.fangtang.tv.sdk.base.app.producers.FetchAppConfigListProducer;
import com.fangtang.tv.sdk.base.app.producers.FetchAppInfoListProducer;
import com.fangtang.tv.sdk.base.app.producers.FetchInstalledAppListProducer;
import com.fangtang.tv.sdk.base.app.producers.InstallAppProducer;
import com.fangtang.tv.sdk.base.app.producers.LocalFetchAppInfoProducer;
import com.fangtang.tv.sdk.base.app.producers.PersistentAppInfoProducer;
import com.fangtang.tv.sdk.base.app.producers.ReportInstalledAppListProducer;
import com.fangtang.tv.sdk.base.app.producers.ValidateDownloadAppProducer;
import com.fangtang.tv.sdk.base.app.producers.ValidateLocalAppProducer;
import com.fangtang.tv.sdk.base.core.Consumer;
import com.fangtang.tv.sdk.base.core.ExecutorSupplier;
import com.fangtang.tv.sdk.base.core.Producer;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.logging.FLog;
import com.fangtang.tv.sdk.base.util.MarketUtil;
import com.fangtang.tv.sdk.download.core.DownloadManager;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.view.DownloadViewManager;
import com.fangtang.tv.sdk.download.view.model.DownloadViewModel;

import java.util.List;

public class AppManager implements IAppManager {

    private static final String TAG = AppManager.class.getSimpleName();

    private static AppManager instance;
    private DownloadManager downloadManager;
    private DownloadViewManager downloadViewManager;
    private IInstallManager appInstallManager;

    private ExecutorSupplier mExecutorSupplier;
    private DeviceManager deviceManager;
    private boolean reportInstalledApp;

    private Context context;
    private Handler mHandler;

    private AppManager() {
    }

    public synchronized static AppManager getInstance() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }


    public synchronized void init(AppConfiguration configuration) {
        this.context = configuration.context.getApplicationContext();

        this.mExecutorSupplier = configuration.executorSupplier;
        this.deviceManager = configuration.deviceManager;
        this.appInstallManager = configuration.appInstallManager;
        this.reportInstalledApp = configuration.reportInstalledApp;
        this.mHandler = new Handler(Looper.getMainLooper());

        //download
        DownloaderConfiguration downloaderConfiguration =
                new DownloaderConfiguration.Builder(context)
                        .threadPoolSize(30)
                        .maxDownloadingSize(3)
                        .build();
        downloadManager = DownloadManager.getInstance();
        downloadManager.init(downloaderConfiguration);

        //download view
        downloadViewManager = DownloadViewManager.getInstance();
        downloadViewManager.init(context);

        //上报应用信息
        if (reportInstalledApp) {
            newReportInstalledAppListProducerSequence();
        }

        persistentProducerSequence();
    }

    private void persistentProducerSequence() {
        newPersistentProducerSequence().produceResults(
                new Consumer<List<AppInfoBean>>() {
                    @Override
                    public void onNewResult(List<AppInfoBean> newResult, int status) {
                        if (FLog.isLoggable(FLog.VERBOSE)) {
                            FLog.e(TAG, "--------persistentProducerSequence---onNewResult--->>>>>" + newResult);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        if (FLog.isLoggable(FLog.VERBOSE)) {
                            FLog.e(TAG, "--------persistentProducerSequence---onFailure--->>>>>");
                        }
                    }

                    @Override
                    public void onCancellation() {

                    }

                    @Override
                    public void onProgressUpdate(float progress) {

                    }
                }, null);
    }

    private void newReportInstalledAppListProducerSequence() {
        FetchAppConfigListProducer fetchAppListConfigProducer =
                new FetchAppConfigListProducer(deviceManager);

        FetchInstalledAppListProducer fetchInstalledAppListProducer =
                new FetchInstalledAppListProducer(
                        this,
                        mExecutorSupplier.forBackgroundTasks(),
                        fetchAppListConfigProducer);
        ReportInstalledAppListProducer reportInstalledAppListProducer =
                new ReportInstalledAppListProducer(deviceManager, fetchInstalledAppListProducer);
        reportInstalledAppListProducer.produceResults(new Consumer<Boolean>() {
            @Override
            public void onNewResult(Boolean newResult, int status) {
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.e(TAG, "--------newReportInstalledAppListProducerSequence---onNewResult--->>>>>" + newResult);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.e(TAG, "--------newReportInstalledAppListProducerSequence---onFailure--->>>>>");
                }
            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onProgressUpdate(float progress) {

            }
        }, null);
    }


    @Override
    public void installApp(final AppInfoBean appInfoBean, final InstallAppListener listener) {
        LocalFetchAppInfoProducer localFetchAppInfoProducer =
                new LocalFetchAppInfoProducer(context, mExecutorSupplier.forBackgroundTasks(), appInfoBean);
        ValidateLocalAppProducer validateLocalAppProducer =
                new ValidateLocalAppProducer(downloadManager, mExecutorSupplier.forBackgroundTasks(), localFetchAppInfoProducer);
        DownloadAppProducer downloadAppProducer =
                new DownloadAppProducer(downloadManager, downloadViewManager, validateLocalAppProducer);
        ValidateDownloadAppProducer validateDownloadAppAppProducer =
                new ValidateDownloadAppProducer(mExecutorSupplier.forBackgroundTasks(), downloadAppProducer);
        InstallAppProducer installAppProducer =
                new InstallAppProducer(validateDownloadAppAppProducer, downloadViewManager, appInstallManager);
        installAppProducer.produceResults(new Consumer<AppInfoBean>() {
            @Override
            public void onNewResult(AppInfoBean newResult, int status) {
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.v(TAG, status + "----installApp------>>>>" + newResult);
                }

                if (status == Consumer.IS_DOWNLOADING || status == Consumer.IS_DOWNLOAD_DONE) {
                    return;
                }

                try {
                    //delete file
                    downloadManager.delete(appInfoBean.md5);
                } catch (Throwable e) {
                    e.printStackTrace();
                }


                if (listener != null) {
                    listener.onInstallAppSuccess(appInfoBean);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                if (FLog.isLoggable(FLog.VERBOSE)) {
                    FLog.v(TAG, "----installApp-----onFailure->>>>" + appInfoBean);
                }

                try {
                    //delete file
                    downloadManager.delete(appInfoBean.md5);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //update UI
                        DownloadViewModel downloadViewModel = new DownloadViewModel();
                        downloadViewModel.id = appInfoBean.md5;
                        downloadViewModel.name = appInfoBean.name;
                        downloadViewManager.downloadError(downloadViewModel);
                    }
                });

                if (listener != null) {
                    listener.onInstallAppError(appInfoBean, t);
                }
            }

            @Override
            public void onCancellation() {
                try {
                    //delete file
                    downloadManager.delete(appInfoBean.md5);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgressUpdate(float progress) {

            }
        }, null);
    }

    @Override
    public boolean isAppInstalled(AppInfoBean appInfoBean) {
        if (appInfoBean == null || TextUtils.isEmpty(appInfoBean.packageName)) {
            return false;
        }
        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(appInfoBean.packageName);
        if (appInfo == null) {
            return false;
        }

        if (!TextUtils.isEmpty(appInfoBean.versionCode)) {
            try {
                int versionCode = Integer.parseInt(appInfoBean.versionCode);
                if (appInfo.getVersionCode() < versionCode) {
                    return false;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean isAppInstalled(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
//        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(packageName);
//        if (appInfo == null) {
//            return false;
//        }
//        return true;

        // 换成这个不会打印错误警告
        return MarketUtil.isAppInstalled(packageName);
    }

    private FetchAppInfoListProducer newFetchAppListProducer() {
        return new FetchAppInfoListProducer(deviceManager);
    }

    private PersistentAppInfoProducer newPersistentProducer(Producer<List<AppInfoBean>> inputProducer) {
        return new PersistentAppInfoProducer(context, mExecutorSupplier.forBackgroundTasks(), inputProducer);
    }

    private Producer<List<AppInfoBean>> newPersistentProducerSequence() {
        return newPersistentProducer(newFetchAppListProducer());
    }
}
