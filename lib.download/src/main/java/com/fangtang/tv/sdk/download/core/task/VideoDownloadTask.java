package com.fangtang.tv.sdk.download.core.task;

import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.DownloadTask;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.download.BaseDownloadEngine;
import com.fangtang.tv.sdk.download.core.download.IDownloader;
import com.fangtang.tv.sdk.download.utils.DL;
import com.fangtang.tv.sdk.download.utils.IoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class VideoDownloadTask extends BaseDownloadTask {

    private static final int RETRY_COUNT = 3;

    private final BaseDownloadEngine engine;
    private final Download downloadInfo;
    private DownloadTask taskInfo;

    private final IDownloader downloader;
    private final DownloaderConfiguration configuration;

    private final String url;
    private String path;
    private IDownloadDBHelper downloadDBHelper;

    private int executionCount;

    public VideoDownloadTask(BaseDownloadEngine engine, Download downloadInfo, DownloadTask taskInfo) {
        this.engine = engine;
        this.downloadInfo = downloadInfo;
        this.taskInfo = taskInfo;
        this.downloadDBHelper = engine.configuration.downloadDBHelper;

        this.path = downloadInfo.getPath();
        this.configuration = engine.configuration;
        this.downloader = configuration.downloader;
        this.url = taskInfo.getUrl();
    }

    @Override
    public void run() {
        if (isPaused()) {
            // pause
            taskInfo.setStatus(Constant.TASK_STATE_PAUSE);
            updateTaskStatus();
            return;
        }

        try {
            makeRequestWithRetries();
        } catch (Throwable e) {
            postException(e);
        }
    }

    /**
     * make request
     *
     * @throws Exception
     */
    private void makeRequest() throws IOException {

        DL.e(executionCount + "----executionCount----" + taskInfo.getRange() + "----getRange------getDownloadSize---"
                + taskInfo.getDownloadSize() + "--下载的位置--" + "#------下载开始------->>>>>>>>" + taskInfo.getDownloadTaskId());

        //pause
        if (isPaused()) {
            taskInfo.setStatus(Constant.TASK_STATE_PAUSE);
            updateTaskStatus();
            return;
        }

        //downloading
        taskInfo.setStatus(Constant.TASK_STATE_DOWNLOADING);
        updateTaskStatus();

        InputStream inputStream = downloader.getStream(url, (taskInfo.getRange() + taskInfo.getDownloadSize()),
                (taskInfo.getRange() + taskInfo.getFileLength()), null);
        byte[] buffer = new byte[IoUtils.DEFAULT_BUFFER_SIZE];
        int offset;

        File cacheFile = new File(path);

        RandomAccessFile threadFile = new RandomAccessFile(cacheFile, "rwd");
        threadFile.seek(taskInfo.getRange() + taskInfo.getDownloadSize());

        while (!isPaused() && (offset = inputStream.read(buffer, 0, IoUtils.DEFAULT_BUFFER_SIZE)) != -1) {
            threadFile.write(buffer, 0, offset);
            taskInfo.setDownloadSize(taskInfo.getDownloadSize() + offset);
            //更新下载进度
            downloadDBHelper.updateDownloadTask(taskInfo);
            //更新下载的总长度
            engine.updateDownloadSize(offset);
        }

        DL.e(taskInfo.getDownloadTaskId() + "#-----5----VideoDownloadTask----------->>>>>" + System.currentTimeMillis());
        threadFile.close();
        IoUtils.closeSilently(inputStream);

        //暂停下载
        if (isPaused()) {
            taskInfo.setStatus(Constant.TASK_STATE_PAUSE);
            updateTaskStatus();
        }
        //下载完成
        else {
            taskInfo.setStatus(Constant.TASK_STATE_FINISH);
            updateTaskStatus();
            downloadDBHelper.deleteDownloadTask(taskInfo);
        }
        DL.e(taskInfo.getDownloadSize() + "#------下载结束------->>>>>>>>" + taskInfo.getDownloadTaskId() + "#------->>>>" + downloadInfo.getDownloadTaskList().size());
    }

    private void makeRequestWithRetries() throws IOException {
        boolean retry = true;
        IOException cause = null;
        try {
            while (retry) {
                try {
                    makeRequest();
                    return;
                } catch (UnknownHostException e) {
                    ++executionCount;
                    cause = new IOException("UnknownHostException exception: " + e.getMessage());
                    retry = (executionCount <= RETRY_COUNT);
                } catch (NullPointerException e) {
                    ++executionCount;
                    cause = new IOException("NPE in HttpClient: " + e.getMessage());
                    retry = (executionCount <= RETRY_COUNT);
                } catch (SocketTimeoutException e) {
                    ++executionCount;
                    retry = (executionCount <= RETRY_COUNT);
                    cause = e;
                } catch (SocketException e) {
                    ++executionCount;
                    retry = (executionCount <= RETRY_COUNT);
                    cause = e;
                } catch (FileNotFoundException e) {
                    ++executionCount;
                    retry = (executionCount <= RETRY_COUNT);
                    cause = e;
                } catch (IOException e) {
                    ++executionCount;
                    retry = (executionCount <= RETRY_COUNT);
                    cause = e;
                }
            }
        } catch (Exception e) {
            cause = new IOException("Unhandled exception: " + e.getMessage());
        }
        DL.e("#-------请求的异常---------->>>>>>" + cause);
        // cleaned up to throw IOException
        throw (cause);
    }

    /**
     * handle exception
     *
     * @param e
     */
    private void postException(Throwable e) {
        taskInfo.setStatus(Constant.TASK_STATE_EXCEPTION);
        updateTaskStatus();
    }

    /**
     * update download task status
     */
    private void updateTaskStatus() {
        downloadDBHelper.saveDownloadTask(taskInfo);
        engine.updateDownloadTaskStatus(taskInfo);
    }
}
