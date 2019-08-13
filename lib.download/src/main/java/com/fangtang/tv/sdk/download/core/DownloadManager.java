package com.fangtang.tv.sdk.download.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.IDownloadListener;
import com.fangtang.tv.sdk.download.IDownloadService;
import com.fangtang.tv.sdk.download.core.listener.DownloadListener;
import com.fangtang.tv.sdk.download.core.service.DownloadService;
import com.fangtang.tv.sdk.download.core.service.DownloadServiceManager;
import com.fangtang.tv.sdk.download.utils.DL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DownloadManager
 */
public class DownloadManager {

    private static final String TAG = "Download_Manager";
    private static final String LOG_INIT_CONFIG = "Initialize DownloadManager with configuration";
    private static final String LOG_DESTROY = "Destroy DownloadManager";
    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize DownloadManager which had already been initialized before. " + "To re-init downloader with new configuration call DownloaderManager.onDestroy() at first.";
    private static final String ERROR_NOT_INIT = "DownloadManager must be init with configuration before using";
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "DownloadManager configuration can not be initialized with null";

    private DownloaderConfiguration configuration;
    private IDownloadService downloadService;

    private static DownloadManager instance;
    private DownloadServiceManager serviceManager;

    private Map<Object, DownloadListener> downloadListenerList = new HashMap<>();
    private Handler mHandler;

    private DownloadManager() {
    }

    public synchronized static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownloadManager.class) {
                if (instance == null) {
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    public synchronized void init(DownloaderConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null) {
            DL.d(LOG_INIT_CONFIG);
            this.configuration = configuration;
            initConfiguration();
        } else {
            DL.w(WARNING_RE_INIT_CONFIG);
        }
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void initConfiguration() {
        serviceManager = DownloadServiceManager.getInstance();
        serviceManager.init(configuration);
        //启动服务
        bindService(configuration.context);
    }

    private void checkConfiguration() {
        if (configuration == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }

    /**
     * 回调
     */
    private IDownloadListener mCallback = new IDownloadListener.Stub() {

        @Override
        public void onDownloadStatusChanged(final Download info) throws RemoteException {
            if (info != null && !TextUtils.isEmpty(info.getDownloadId())) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadListener listener = downloadListenerList.get(info.getDownloadId());
                        if (listener != null) {
                            listener.onDownloadStatusChanged(info);
                        }
                    }
                });
            }
        }

        @Override
        public void onDownloadListChanged() throws RemoteException {
            try {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (DownloadListener listener : downloadListenerList.values()) {
                            try {
                                listener.onDownloadListChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgressUpdate(final Download info) throws RemoteException {
            if (info != null && !TextUtils.isEmpty(info.getDownloadId())) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DownloadListener listener = downloadListenerList.get(info.getDownloadId());
                        if (listener != null) {
                            listener.onProgressUpdate(info);
                        }
                    }
                });
            }
        }
    };

    /**
     * 链接
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DL.e("#-----service-----onServiceDisconnected-------->>>>>>>");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DL.e("#-----service---onServiceConnected---------->>>>>>>");
            downloadService = IDownloadService.Stub.asInterface(service);
            registerListener();
        }
    };

    /**
     * 绑定服务
     *
     * @param context
     */
    private void bindService(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void registerListener() {
        try {
            downloadService.registerListener(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unregisterListener() {
        try {
            downloadService.unregisterListener(mCallback);
        } catch (RemoteException e) {
        } catch (Exception e) {
        }
    }

    public void onDestory() {
        if (configuration != null) DL.d(LOG_DESTROY);
        configuration = null;
        downloadListenerList.clear();
        unregisterListener();
    }


    public void registerDownloadListener(DownloadListener downloadListener) {
        if (downloadListener != null) {
            downloadListenerList.put(downloadListener.tag, downloadListener);
        }
    }

    public void unregisterDownloadListener(DownloadListener downloadListener) {
        if (downloadListenerList != null) {
            downloadListenerList.remove(downloadListener.tag);
        }
    }


    /**
     * 下载视频
     *
     * @param info
     * @return
     */
    public void download(Download info) {
        checkConfiguration();
        try {
            DL.e("#------DownloadManager---download--Download->>>>>>>" + info);
            DL.e("#------DownloadManager---download--downloadService->>>>>>>" + downloadService);
            downloadService.download(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始下载
     *
     * @param taskId
     * @return
     */
    public void start(String taskId) {
        checkConfiguration();
        try {
            downloadService.start(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始所有的下载
     *
     * @return
     */
    public void startAll() {
        checkConfiguration();
        try {
            downloadService.startAll();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止
     *
     * @param taskId
     * @return
     */
    public void pause(String taskId) {
        checkConfiguration();
        try {
            downloadService.pause(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止所有的下载
     *
     * @return
     */
    public void pauseAll() {
        checkConfiguration();
        try {
            downloadService.pauseAll();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除下载
     *
     * @param downloadId
     * @return
     */
    public void delete(String downloadId) {
        checkConfiguration();
        try {
            downloadService.delete(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有的下载
     *
     * @return
     */
    public void deleteAll() {
        checkConfiguration();
        try {
            downloadService.deleteAll();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取下载列表
     *
     * @return
     */
    public List<Download> getDownloadList() {
        checkConfiguration();
        try {
            return downloadService.getDownloadList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Download getDownload(String downloadId) {
        checkConfiguration();
        try {
            return downloadService.getDownload(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DownloaderConfiguration getConfiguration() {
        return configuration;
    }
}
