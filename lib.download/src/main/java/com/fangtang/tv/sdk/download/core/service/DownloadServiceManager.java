package com.fangtang.tv.sdk.download.core.service;

import android.text.TextUtils;

import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.IDownloadListener;
import com.fangtang.tv.sdk.download.core.DefaultConfigurationFactory;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.IDownloadManager;
import com.fangtang.tv.sdk.download.core.assist.FailReason;
import com.fangtang.tv.sdk.download.core.assist.deque.LinkedBlockingDeque;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.download.DownloadEngine;
import com.fangtang.tv.sdk.download.core.listener.DownloadDBInitCallback;
import com.fangtang.tv.sdk.download.core.listener.DownloadListInitCallback;
import com.fangtang.tv.sdk.download.core.task.DownloadCancelTask;
import com.fangtang.tv.sdk.download.core.task.DownloadInitTask;
import com.fangtang.tv.sdk.download.core.task.DownloadListInitTask;
import com.fangtang.tv.sdk.download.utils.DL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DownloadServiceManager
 */
public class DownloadServiceManager implements IDownloadManager, Runnable {

    private DownloaderConfiguration configuration;
    private static DownloadServiceManager instance;

    private LinkedBlockingDeque<Download> downloadQueue;
    private List<Download> allDownloadList;
    private List<Download> downloadingList;
    private int maxTaskSize;

    private Executor taskExecutor;
    private Executor taskDistributor;
    private volatile boolean executorRunning;
    protected final AtomicBoolean isStop = new AtomicBoolean();
    private IDownloadListener downloadListener;
    private IDownloadDBHelper downloadDBHelper;


    private DownloadServiceManager() {
    }

    public synchronized static DownloadServiceManager getInstance() {
        if (instance == null) {
            synchronized (DownloadServiceManager.class) {
                if (instance == null) {
                    instance = new DownloadServiceManager();
                }
            }
        }
        return instance;
    }

    public synchronized void init(DownloaderConfiguration configuration) {
        this.configuration = configuration;
        this.maxTaskSize = configuration.maxDownloadingSize;
        this.taskExecutor = configuration.taskExecutorForLoadVideoInfo;
        this.taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
        this.downloadQueue = new LinkedBlockingDeque<>();

        this.allDownloadList = Collections.synchronizedList(new ArrayList<Download>());
        this.downloadingList = Collections.synchronizedList(new ArrayList<Download>());
        this.downloadDBHelper = configuration.downloadDBHelper;

        // load download list from db
        initDownloadList();
    }

    @Override
    public void registerListener(IDownloadListener listener) {
        downloadListener = listener;
    }

    @Override
    public void unregisterListener(IDownloadListener listener) {
        downloadListener = null;
    }

    @Override
    public void download(Download download) {
        if (allDownloadList.contains(download)) {
            int index = allDownloadList.indexOf(download);
            if (index != -1 && allDownloadList.get(index) != null) {
                Download d = allDownloadList.get(index);
                //如果已经下载完成，则直接返回
                if (d.getStatus() == Constant.DOWNLOAD_STATE_FINISH
                        || d.getStatus() == Constant.DOWNLOAD_STATE_DOWNLOADING) {
                    onDownloadStatusChanged(d);
                    return;
                }
            } else {
                allDownloadList.remove(download);
            }

            DL.e("#------如果在下载队列，则开始下载----->>>>>>>" + download);
            start(download.getDownloadId());
            return;
        }

        download.downloadOptions = configuration.defaultDownloadOptions;
        DownloadInitTask downloadInitTask = new DownloadInitTask(configuration, download, downloadInitCallback);
        taskExecutor.execute(downloadInitTask);
    }

    @Override
    public void start(String downloadId) {
        for (Download download : allDownloadList) {
            if (!TextUtils.isEmpty(download.getDownloadId()) && download.getDownloadId().equals(downloadId)) {
                DL.e("#------开始下载的状态------>>>>>>>" + download);
                addDownloadToQueue(download);
                startDownloadQueue();
                break;
            }
        }
    }

    @Override
    public void startAll() {
    }

    @Override
    public void pause(String downloadId) {
        for (Download download : allDownloadList) {
            if (!TextUtils.isEmpty(download.getDownloadId()) && download.getDownloadId().equals(downloadId)) {
                switch (download.getStatus()) {
                    case Constant.DOWNLOAD_STATE_DOWNLOADING:
                        DownloadEngine engine = download.getDownloadEngine();
                        if (engine != null) {
                            engine.pause();
                        }
                        download.setDownloadEngine(null);
                        downloadingList.remove(download);
                        break;
                }
                break;
            }
        }
        DL.e("#-------------下载队列的长度------------->>>>>" + downloadingList.size());
        startDownloadQueue();
    }

    @Override
    public void pauseAll() {
        stopDownloadQueue();
        for (Download download : downloadingList) {
            DownloadEngine engine = download.getDownloadEngine();
            if (engine != null) {
                engine.pause();
            }
            download.setDownloadEngine(null);
        }
        downloadingList.clear();
    }

    @Override
    public void delete(final String downloadId) {
        DL.e(allDownloadList + "#---1--删除--delete->>>>>" + downloadId);

        int index = -1;
        for (int i = 0; i < allDownloadList.size(); i++) {
            Download download = allDownloadList.get(i);
            if (!TextUtils.isEmpty(download.getDownloadId())
                    && download.getDownloadId().equals(downloadId)) {
                index = i;
                DL.e("#----2-删除--delete->>>>>" + index);
                switch (download.getStatus()) {
                    //如果正在下载，则暂停
                    case Constant.DOWNLOAD_STATE_DOWNLOADING:
                        DownloadEngine engine = download.getDownloadEngine();
                        if (engine != null) {
                            engine.pause();
                        }
                        download.setDownloadEngine(null);
                        downloadingList.remove(download);
                        break;
                    default:
                        downloadQueue.remove(download);
                        break;
                }
                //
                break;
            }
        }
        DL.e("#---3--删除--->>>>>" + index);
        if (index != -1) {
            allDownloadList.remove(index);
        }
        try {
            Download deleteDownload = new Download();
            deleteDownload.setDownloadId(downloadId);

            DownloadCancelTask cancelTask = new DownloadCancelTask(this, configuration, deleteDownload);
            taskExecutor.execute(cancelTask);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Download> getDownloadList() {
        return allDownloadList;
    }

    @Override
    public Download getDownload(String downloadId) {
        DL.e(downloadId + "#------downloadInitCallback--onSuccess-->>>>>>>" + allDownloadList);
        if (allDownloadList == null) {
            return null;
        }
        for (int i = 0; i < allDownloadList.size(); i++) {
            Download download = allDownloadList.get(i);
            if (!TextUtils.isEmpty(download.getDownloadId())
                    && download.getDownloadId().equals(downloadId)) {
                return download;
            }
        }
        return null;
    }

    public void onDestroy() {

    }

    /**
     * init download list from db
     */
    private void initDownloadList() {
        DownloadListInitTask downloadInitTask = new DownloadListInitTask(configuration, downloadListInitCallback);
        taskExecutor.execute(downloadInitTask);
    }

    /**
     * init download callback
     */
    private DownloadListInitCallback downloadListInitCallback = new DownloadListInitCallback() {
        @Override
        public void onSuccess(List<Download> downloadList) {
            if (downloadList != null && downloadList.size() > 0) {
                allDownloadList.addAll(downloadList);
                onDownloadListChanged();
                for (Download download : downloadList) {
                    switch (download.getStatus()) {
                        case Constant.DOWNLOAD_STATE_INIT:
                        case Constant.DOWNLOAD_STATE_VIDEO_INIT:
                        case Constant.DOWNLOAD_STATE_DOWNLOADING:
                            addDownloadToQueue(download);
                            startDownloadQueue();
                            break;
                        case Constant.DOWNLOAD_STATE_PAUSE:
                            break;
                        case Constant.DOWNLOAD_STATE_FINISH:
                            break;
                        case Constant.DOWNLOAD_STATE_EXCEPTION:
                        case Constant.DOWNLOAD_STATE_INIT_ERROR:
                        case Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR:
                            break;
                    }
                }
            }
        }

        @Override
        public void onFailure(FailReason failReason) {
            //empty download task, do nothing
        }
    };

    /**
     * init download into db
     */
    private DownloadDBInitCallback downloadInitCallback = new DownloadDBInitCallback() {
        @Override
        public void onSuccess(Download download) {
            DL.e("#------downloadInitCallback--onSuccess-->>>>>>>" + download);
            allDownloadList.add(download);
            onDownloadListChanged();
            addDownloadToQueue(download);
            startDownloadQueue();
        }

        @Override
        public void onFailure(Download download, FailReason failReason) {
            DL.e("#------downloadInitCallback--onFailure-->>>>>>>" + download);
            download.setStatus(Constant.DOWNLOAD_STATE_INIT_ERROR);
            onDownloadStatusChanged(download);
        }
    };


    /**
     * add download to queue
     */
    public void addDownloadToQueue(Download download) {
        try {
            DL.e("#-------添加到下载队列------->>>>" + download);
            if (downloadQueue.contains(download)) {
                return;
            }
            downloadQueue.putLast(download);
            if (downloadListener != null) {
                downloadListener.onDownloadStatusChanged(download);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * start queue loop
     */
    public void startDownloadQueue() {
        DL.e("---------开始下载队列--------->>>>>>");
        if (!isStop.get()) {
            isStop.set(true);
        }
        if (!executorRunning) {
            executorRunning = true;
            taskDistributor.execute(DownloadServiceManager.this);
        } else {
            DL.e("-->>>download queue is running!");
        }
    }

    /**
     * stop download queue
     */
    public void stopDownloadQueue() {
        if (isStop.get()) {
            isStop.set(false);
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (isStop.get()) {
                    if (downloadingList.size() >= maxTaskSize) {
                        DL.e("#--------超过下载队列最大任务数--------->>>>>>" + downloadingList.size());
                        executorRunning = false;
                        return;
                    }
                    Download download = downloadQueue.poll();
                    if (download == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            download = downloadQueue.poll();
                            if (download == null) {
                                executorRunning = false;
                                DL.e("#--------下载队列没有内容--------->>>>>>");
                                return;
                            }
                        }
                    }
                    DL.e("---------初始化下载引擎-------->>>>>>");
                    DownloadEngine downloadEngine = new DownloadEngine(this, configuration, downloadListener);
                    downloadEngine.downloadVideo(download);
                    download.setDownloadEngine(downloadEngine);
                    downloadingList.add(download);
                }
                executorRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            executorRunning = false;
        }
    }

    public void onDownloadStatusChanged(Download download) {
        try {
            switch (download.getStatus()) {
                case Constant.DOWNLOAD_STATE_FINISH:
                case Constant.DOWNLOAD_STATE_INIT_ERROR:
                case Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR:
                case Constant.DOWNLOAD_STATE_EXCEPTION:
                    //remove from downloading queue
                    downloadingList.remove(download);
                    startDownloadQueue();
                    break;
            }

            if (downloadListener != null) {
                DL.e(download.getDownloadId() + "#-----VID----下载状态改变----Status--->>>>>>" + download.getStatus());
                downloadListener.onDownloadStatusChanged(download);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载列表改变
     */
    public void onDownloadListChanged() {
        try {
            if (downloadListener != null) {
                downloadListener.onDownloadListChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
