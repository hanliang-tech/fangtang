package com.fangtang.tv.sdk.download.core.db;


import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.DownloadTask;

import java.util.List;

/**
 * 下载数据库操作接口
 * Created by liulipeng on 15-9-9.
 */
public interface IDownloadDBHelper {

    //保存下载
    void saveDownload(Download download);

    //删除
    void deleteDownload(Download download);

    //更新
    void updateDownload(Download download);

    //更新
    void updateDownloadFileLength(Download download);

    //更新
    void updateDownloadDownloadSize(Download download);

    //查询
    List<Download> queryDownload(Download download);

    //查询所有
    List<Download> queryAllDownload();

    //查询所有
    List<Download> queryAllDownloadAndTask();

    //批量保存下载队列
    void saveDownloadTask(List<DownloadTask> taskList);

    //查询下载任务
    List<DownloadTask> queryDownloadTask(DownloadTask task);

    //保存下载队列
    void saveDownloadTask(DownloadTask task);

    //删除
    void deleteDownloadTask(Download download);

    //更新下载队列
    void updateDownloadTask(DownloadTask task);

    //删除下载队列
    void deleteDownloadTask(DownloadTask task);

    //查询所有
    List<DownloadTask> queryAllDownloadTask(Download download);
}
