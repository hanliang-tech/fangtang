package com.fangtang.tv.sdk.download.core.task;


import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.assist.ContentLengthAndTypeInputStream;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.download.DownloadEngine;
import com.fangtang.tv.sdk.download.core.download.IDownloader;
import com.fangtang.tv.sdk.download.core.listener.DownloadInfoInitCallback;
import com.fangtang.tv.sdk.download.utils.DL;
import com.fangtang.tv.sdk.download.utils.HttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class DownloadInfoTask extends BaseDownloadTask {

    private static final int RETRY_COUNT = 3;

    private DownloaderConfiguration configuration;
    private Download download;
    private DownloadInfoInitCallback initCallback;
    private IDownloadDBHelper downloadDBHelper;
    private IDownloader downloader;
    private File cacheDir;

    private int executionCount;
    private DownloadEngine downloadEngine;

    public DownloadInfoTask(DownloaderConfiguration configuration, DownloadEngine downloadEngine, Download download, DownloadInfoInitCallback initCallback) {
        this.configuration = configuration;
        this.download = download;
        this.initCallback = initCallback;
        this.downloadDBHelper = configuration.downloadDBHelper;
        this.downloader = configuration.downloader;
        this.cacheDir = configuration.cacheDir;
        this.downloadEngine = downloadEngine;
    }

    @Override
    public void run() {
        if (isPaused()) {
            return;
        }
        try {
            makeRequestWithRetries();
        } catch (Throwable e) {
            postException(e);
        }
    }

    private void makeRequest() throws Exception {
        if (isPaused()) {
            return;
        }
        DL.e("#------DownloadInfoTask------makeRequest---->>>>>" + download);
        ContentLengthAndTypeInputStream inputStream =
                (ContentLengthAndTypeInputStream) downloader.getStream(download.getUrl(), null);

        handleSingleVideo(inputStream);

        //数据库更新
        download.setStatus(Constant.DOWNLOAD_STATE_VIDEO_INIT);
        downloadDBHelper.updateDownload(download);
        onDownloadStatusChanged(download);

        DL.e("#------DownloadInfoTask------更新数据库视频信息--->>>>>");
        if (initCallback != null) {
            initCallback.onSuccess(download);
        }
    }

    /**
     * 处理单个视频
     *
     * @param inputStream
     * @throws Exception
     */
    private void handleSingleVideo(ContentLengthAndTypeInputStream inputStream) throws Exception {
        DL.e("#------DownloadInfoTask------普通视频的格式---->>>>>");
        download.setFileLength(inputStream.available());
        String fileName = configuration.diskCacheFileNameGenerator.generate(download);
        DL.e("#------DownloadInfoTask------创建视频文件--->>>>>cacheDir:" + cacheDir + "---fileName--->>>" + fileName);
        File videoFile = new File(cacheDir, fileName);
        try {
            if (!configuration.supportBreakPoint) {
                if (videoFile.exists() && videoFile.isFile()) {
                    boolean del = videoFile.delete();
                    DL.e("#------DownloadInfoTask----删除之前存在的文件--del--->>>>>" + del);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (!cacheDir.exists()) {
            DL.e("#------DownloadInfoTask-----目录不存在-->>>>>" + cacheDir.getAbsolutePath());
            cacheDir.mkdirs();
        } else {
            DL.e("#------DownloadInfoTask-----目录已经存在-->>>>>" + cacheDir.getAbsolutePath());
        }

        try {
            if (!videoFile.exists()) {
                DL.e("#------DownloadInfoTask-----文件不存在，重新创建文件-->>>>>" + videoFile.getAbsolutePath());
                videoFile.createNewFile();
            } else {
                DL.e("#------DownloadInfoTask-----文件已经存在-->>>>>" + videoFile.getAbsolutePath());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        RandomAccessFile randOut = new RandomAccessFile(videoFile, "rw");
        randOut.setLength(download.getFileLength());
        randOut.close();
        download.setPath(videoFile.getAbsolutePath());

        boolean canRanges = HttpUtils.acceptRanges(inputStream.getAcceptRanges());
        if (canRanges) {
            DL.e("#------DownloadInfoTask------支持分段下载--->>>>>");
            download.setType(Constant.VIDEO_TYPE_RANGE);
        } else {
            DL.e("#------DownloadInfoTask------不支持分段下载--->>>>>");
            download.setType(Constant.VIDEO_TYPE_NORMAL);
        }
    }

    private void makeRequestWithRetries() throws Exception {
        boolean retry = true;
        Exception cause = null;
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
        //TODO
        cause.printStackTrace();
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
        download.setStatus(Constant.DOWNLOAD_STATE_VIDEO_INIT_ERROR);
        downloadDBHelper.updateDownload(download);
        onDownloadStatusChanged(download);
        if (initCallback != null) {
            initCallback.onFailure(download, null);
        }
    }

    /**
     * 下载的状态改变
     *
     * @param download
     */
    public void onDownloadStatusChanged(Download download) {
        downloadEngine.onDownloadStatusChanged(download);
    }
}
