package com.fangtang.tv.sdk.download.core.db;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.DownloadTask;

import java.util.List;

public class NoOpDownloadDBHelper implements IDownloadDBHelper {

    private static NoOpDownloadDBHelper defaultInstance;

    private NoOpDownloadDBHelper() {

    }

    public static NoOpDownloadDBHelper getInstance() {
        if (defaultInstance == null) {
            synchronized (NoOpDownloadDBHelper.class) {
                if (defaultInstance == null) {
                    defaultInstance = new NoOpDownloadDBHelper();
                }
            }
        }
        return defaultInstance;
    }

    //保存
    @Override
    public void saveDownload(Download download) {

    }


    //删除
    @Override
    public void deleteDownload(Download download) {

    }

    //更新
    @Override
    public void updateDownload(Download download) {

    }

    //更新
    @Override
    public void updateDownloadFileLength(Download download) {

    }

    //更新
    @Override
    public void updateDownloadDownloadSize(Download download) {

    }

    @Override
    public List<Download> queryDownload(Download download) {
        return null;
    }

    @Override
    public List<Download> queryAllDownload() {
        return null;
    }


    //批量保存下载队列
    @Override
    public void saveDownloadTask(List<DownloadTask> taskList) {
    }

    //删除
    @Override
    public void deleteDownloadTask(Download download) {
    }

    @Override
    public List<DownloadTask> queryDownloadTask(DownloadTask task) {
        return null;
    }

    //保存下载队列
    @Override
    public synchronized void saveDownloadTask(DownloadTask task) {

    }

    //更新下载队列
    @Override
    public void updateDownloadTask(DownloadTask task) {

    }

    //删除下载队列
    @Override
    public void deleteDownloadTask(DownloadTask task) {

    }

    //查询所有
    @Override
    public List<DownloadTask> queryAllDownloadTask(Download download) {
        return null;
    }


    //查询所有下载和下载任务
    @Override
    public List<Download> queryAllDownloadAndTask() {
        return null;
    }

}
