package com.fangtang.tv.sdk.download.core.download;

import android.os.SystemClock;

import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.DownloadTask;
import com.fangtang.tv.sdk.download.IDownloadListener;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.assist.FailReason;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.listener.DownloadInfoInitCallback;
import com.fangtang.tv.sdk.download.core.service.DownloadServiceManager;
import com.fangtang.tv.sdk.download.core.task.BaseDownloadTask;
import com.fangtang.tv.sdk.download.core.task.DownloadInfoTask;
import com.fangtang.tv.sdk.download.core.task.VideoDownloadTask;
import com.fangtang.tv.sdk.download.utils.DL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DownloadEngine extends BaseDownloadEngine {

    private DownloadServiceManager downloadServiceManager;
    private Download downloadInfo;

    private int maxDownloadTaskSize;
    private long fileLength;
    private IDownloadListener downloadListener;
    private IDownloadDBHelper downloadDBHelper;

    private List<DownloadTask> doneDownloadTaskList = Collections.synchronizedList(new ArrayList<DownloadTask>());
    private List<DownloadTask> errorDownloadTaskList = Collections.synchronizedList(new ArrayList<DownloadTask>());

    private DownloadInfoTask downloadInitTask;

    public DownloadEngine(DownloadServiceManager downloadServiceManager,
                          DownloaderConfiguration configuration, IDownloadListener downloadListener) {
        super(configuration);
        this.downloadListener = downloadListener;
        this.downloadDBHelper = configuration.downloadDBHelper;
        this.downloadServiceManager = downloadServiceManager;
    }

    /**
     * 加载视频信息
     *
     * @param downloadInfo
     */
    public void downloadVideo(Download downloadInfo) {
        this.downloadInfo = downloadInfo;
        // need to load video info
        if (downloadInfo.getStatus() <= Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR) {
            initVideoInfo();
        }
        //no need to load video info
        else {
            initVideoDownloadTask();
        }
    }

    /**
     * new donwload runnable
     */
    private void initVideoDownloadTask() {
        if (isPaused()) {
            return;
        }

        DL.e("#---------分配线程下载--------->>>>>>>");
        this.maxDownloadTaskSize = configuration.defaultDownloadOptions.getDownloadTaskSize();
        this.downloadInfo.setStatus(Constant.DOWNLOAD_STATE_DOWNLOADING);
        this.fileLength = downloadInfo.getFileLength();
        onDownloadStatusChanged(downloadInfo);
        switch (downloadInfo.getType()) {
            case Constant.VIDEO_TYPE_NORMAL:
                initNoneRangesVideoTasks();
                break;
            case Constant.VIDEO_TYPE_RANGE:
                initRangesVideoTasks();
                break;
        }
    }

    /**
     * load video info from server
     */
    private void initVideoInfo() {
        if (isPaused()) {
            return;
        }
        DL.e("#---------获取视频信息--------->>>>>>>");
        downloadInitTask = new DownloadInfoTask(configuration, this, downloadInfo, videoInitCallback);
        taskExecutor.execute(downloadInitTask);
    }

    private DownloadInfoInitCallback videoInitCallback = new DownloadInfoInitCallback() {
        @Override
        public void onSuccess(Download download) {
            downloadInfo = download;
            initVideoDownloadTask();
        }

        @Override
        public void onFailure(Download download, FailReason failReason) {
            downloadInfo = download;
            downloadInfo.setStatus(Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR);
            onDownloadStatusChanged(downloadInfo);
        }
    };

    /**
     * 支持断点的单个文件
     */
    private void initRangesVideoTasks() {
        if (isPaused()) {
            return;
        }
        DL.e("#------初始化队列------支持断点的单个文件------------>>>>>>>>>>>>>");
        downloadInfo.downloadTaskRunableList.clear();
        if (downloadInfo.getDownloadTaskList() != null && downloadInfo.getDownloadTaskList().size() > 0) {
            DL.e("#--有数据---1----initRangesVideoTasks------->>>>>>>" + downloadInfo.getDownloadTaskList().size());
            for (DownloadTask taskInfo : downloadInfo.getDownloadTaskList()) {
                if (Constant.TASK_STATE_FINISH == taskInfo.getStatus()) {
                    updateDownloadTaskStatus(taskInfo);
                    continue;
                }
                VideoDownloadTask downloadTask = new VideoDownloadTask(this, downloadInfo, taskInfo);
                downloadInfo.downloadTaskRunableList.add(downloadTask);
                taskInfo.setStatus(Constant.TASK_STATE_INIT);
                updateTaskStatus(taskInfo);
                submit(downloadTask);
            }
        } else {

            //计算每条线程下载的数据长度
            long block = (this.fileLength % maxDownloadTaskSize) == 0
                    ? fileLength / maxDownloadTaskSize
                    : this.fileLength / maxDownloadTaskSize + 1;

            downloadInfo.getDownloadTaskList().clear();

            for (int i = 0; i < maxDownloadTaskSize; i++) {
                DownloadTask taskInfo = new DownloadTask();
                taskInfo.setDownloadTaskId(i + "");
                taskInfo.setDownloadId(downloadInfo.getDownloadId());
                taskInfo.setFileLength(block);
                taskInfo.setRange(block * (i));
                taskInfo.setUrl(downloadInfo.getUrl());
                taskInfo.setStatus(Constant.TASK_STATE_INIT);
                updateTaskStatus(taskInfo);

                downloadInfo.addDownloadTask(taskInfo);

                VideoDownloadTask downloadTask = new VideoDownloadTask(this, downloadInfo, taskInfo);
                downloadInfo.downloadTaskRunableList.add(downloadTask);

                submit(downloadTask);
            }
            DL.e("#-----2---无数据---------->>>>" + downloadInfo.getDownloadTaskList().size());
        }
    }

    /**
     * 不支持断点下载的单个文件
     */
    private void initNoneRangesVideoTasks() {
        if (isPaused()) {
            return;
        }
        downloadInfo.downloadTaskRunableList.clear();
        if (downloadInfo.getDownloadTaskList() != null && downloadInfo.getDownloadTaskList().size() > 0) {
            for (DownloadTask taskInfo : downloadInfo.getDownloadTaskList()) {
                if (Constant.TASK_STATE_FINISH == taskInfo.getStatus()) {
                    updateDownloadTaskStatus(taskInfo);
                    continue;
                }

                taskInfo.setStatus(Constant.TASK_STATE_INIT);
                updateTaskStatus(taskInfo);

                VideoDownloadTask downloadTask = new VideoDownloadTask(this, downloadInfo, taskInfo);
                downloadInfo.downloadTaskRunableList.add(downloadTask);
                submit(downloadTask);
            }
        } else {
            downloadInfo.getDownloadTaskList().clear();

            DownloadTask taskInfo = new DownloadTask();
            taskInfo.setDownloadTaskId("1");
            taskInfo.setDownloadId(downloadInfo.getDownloadId());
            taskInfo.setFileLength(fileLength);
            taskInfo.setRange(0);
            taskInfo.setUrl(downloadInfo.getUrl());
            taskInfo.setStatus(Constant.TASK_STATE_INIT);
            updateTaskStatus(taskInfo);

            downloadInfo.addDownloadTask(taskInfo);

            VideoDownloadTask downloadTask = new VideoDownloadTask(this, downloadInfo, taskInfo);
            downloadInfo.downloadTaskRunableList.add(downloadTask);
            submit(downloadTask);
        }
    }

    @Override
    public boolean pause() {
        isPaused.set(true);

        this.downloadInfo.setStatus(Constant.DOWNLOAD_STATE_PAUSE);
        onDownloadStatusChanged(downloadInfo);

        if (downloadInitTask != null) {
            downloadInitTask.pause();
        }

        if (downloadInfo.downloadTaskRunableList != null &&
                downloadInfo.downloadTaskRunableList.size() > 0) {
            for (BaseDownloadTask downloadTask : downloadInfo.downloadTaskRunableList) {
                downloadTask.pause();
            }
            downloadInfo.downloadTaskRunableList.clear();
        }
        DL.e("#---2--cancel----下载队列的数量--------->>>>>>" + downloadInfo.getDownloadTaskList().size());
        return isPaused();
    }

    /**
     * 更新文件的下载的长度
     *
     * @param downloadSize
     */
    @Override
    public void updateDownloadSize(long downloadSize) {
        downloadInfo.setDownloadSize(downloadInfo.getDownloadSize() + downloadSize);
        downloadDBHelper.updateDownloadDownloadSize(downloadInfo);
        updateDownloadProgress();
    }

    /**
     * 更新文件的总长度
     *
     * @param fileLength
     */
    @Override
    public synchronized void updateFileLength(long fileLength) {
        downloadInfo.setFileLength(downloadInfo.getFileLength() + fileLength);
        downloadDBHelper.updateDownloadFileLength(downloadInfo);
        updateDownloadProgress();
    }


    private void updateDownloadProgress() {
        if (downloadListener != null && downloadInfo != null) {
            long currentTime = SystemClock.elapsedRealtime();
            boolean isNotify = (currentTime - downloadInfo.getLastRefreshTime()) >= 500;
            if (isNotify || downloadInfo.getDownloadSize() == downloadInfo.getFileLength()) {
                try {
                    downloadInfo.setLastRefreshTime(currentTime);
                    downloadListener.onProgressUpdate(downloadInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新下载任务的状态
     *
     * @param downloadTask
     */
    @Override
    public synchronized void updateDownloadTaskStatus(DownloadTask downloadTask) {
        switch (downloadTask.getStatus()) {
            case Constant.TASK_STATE_FINISH:
                doneDownloadTaskList.add(downloadTask);
                break;
            case Constant.TASK_STATE_EXCEPTION:
                errorDownloadTaskList.add(downloadTask);
                break;
        }

        //所有的任务执行完成
        if (doneDownloadTaskList.size() + errorDownloadTaskList.size() >= downloadInfo.getDownloadTaskList().size()) {
            if (errorDownloadTaskList.size() > 0) {
                downloadInfo.setStatus(Constant.DOWNLOAD_STATE_EXCEPTION);
            } else {
                downloadInfo.setStatus(Constant.DOWNLOAD_STATE_FINISH);
            }
            doneDownloadTaskList.clear();
            errorDownloadTaskList.clear();
            onDownloadStatusChanged(downloadInfo);
        }
    }

    /**
     * update task status to db
     *
     * @param taskInfo
     */
    private void updateTaskStatus(DownloadTask taskInfo) {
        downloadDBHelper.saveDownloadTask(taskInfo);
    }

    public void onDownloadStatusChanged(Download download) {
        try {
            downloadDBHelper.saveDownload(download);
            downloadServiceManager.onDownloadStatusChanged(download);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadEngine that = (DownloadEngine) o;
        return !(downloadInfo != null ? !downloadInfo.equals(that.downloadInfo) : that.downloadInfo != null);

    }

    @Override
    public int hashCode() {
        return downloadInfo != null ? downloadInfo.hashCode() : 0;
    }
}
