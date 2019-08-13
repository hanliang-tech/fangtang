package com.fangtang.tv.sdk.download.core.task;

import android.text.TextUtils;

import com.fangtang.tv.sdk.download.Constant;
import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.cache.naming.FileNameGenerator;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.service.DownloadServiceManager;
import com.fangtang.tv.sdk.download.utils.DL;

import java.io.File;
import java.io.IOException;

/**
 * 删除下载
 */
public class DownloadCancelTask extends BaseDownloadTask {

    private static final int RETRY_COUNT = 3;

    private Download download;
    private IDownloadDBHelper downloadDBHelper;

    private int executionCount;
    private DownloadServiceManager serviceManager;
    private File cacheDir;
    private FileNameGenerator nameGenerator;

    public DownloadCancelTask(DownloadServiceManager serviceManager, DownloaderConfiguration configuration, Download download) {
        this.download = download;
        this.serviceManager = serviceManager;
        this.downloadDBHelper = configuration.downloadDBHelper;
        this.cacheDir = configuration.cacheDir;
        this.nameGenerator = configuration.diskCacheFileNameGenerator;
    }

    @Override
    public void run() {
        try {
            makeRequestWithRetries();
        } catch (Throwable e) {
            e.printStackTrace();
            postException(e);
        }
    }

    private void makeRequest() throws Exception {
        DL.e("#---DownloadCancelTask-makeRequest->>>>>" + download);
        //删除数据库
        downloadDBHelper.deleteDownload(download);

        if (!TextUtils.isEmpty(download.getPath())) {
            DL.e("#----1-DownloadCancelTask---delete--->>>>>" + download.getPath());
            try {
                File cacheFile = new File(download.getPath());
                if (cacheFile.exists()) {
                    if (cacheFile.isFile()) {
                        cacheFile.delete();
                    } else {
                        deleteDir(cacheFile);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            try {
                String fileName = nameGenerator.generate(download);
                File cacheFile = new File(cacheDir, fileName);
                DL.e("#----2-DownloadCancelTask---delete--->>>>>" + cacheFile.getAbsolutePath());
                if (cacheFile.exists()) {
                    if (cacheFile.isFile()) {
                        cacheFile.delete();
                    } else {
                        deleteDir(cacheFile);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        download.setStatus(Constant.DOWNLOAD_STATE_CANCEL);
        onDownloadStatusChanged(download);
    }

    /**
     * 删除文件夹
     *
     * @param f
     * @throws IOException
     */
    public void deleteDir(File f) throws IOException {
        if (f.exists() && f.isDirectory()) {//判断是文件还是目录
            if (f.listFiles().length == 0) {//若目录下没有文件则直接删除
                f.delete();
            } else {//若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        deleteDir(delFile[j]);//递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();//删除文件
                }
                f.delete();
            }
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
                } catch (IOException e) {
                    ++executionCount;
                    retry = (executionCount <= RETRY_COUNT);
                    cause = e;
                } catch (Exception e) {
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
        download.setStatus(Constant.DOWNLOAD_STATE_CANCEL);
        onDownloadStatusChanged(download);
    }

    /**
     * 下载的状态改变
     *
     * @param download
     */
    public void onDownloadStatusChanged(Download download) {
        serviceManager.onDownloadStatusChanged(download);
        //列表变化
        serviceManager.onDownloadListChanged();
    }
}
