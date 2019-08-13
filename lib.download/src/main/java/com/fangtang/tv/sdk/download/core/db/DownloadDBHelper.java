package com.fangtang.tv.sdk.download.core.db;

import android.content.Context;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.DownloadTask;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 下载的数据库管理
 *
 * @author liulipeng
 */
public class DownloadDBHelper implements IDownloadDBHelper {

    private static final String APP_DB_NAME = "download.db";
    private static DaoSession daoSession;
    private static DownloadDBHelper defaultInstance;
    private DownloadTaskDao downloadTaskDao;
    private DownloadDao downloadDao;

    private DownloadDBHelper(Context context) {

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, APP_DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();

        downloadTaskDao = daoSession.getDownloadTaskDao();
        downloadDao = daoSession.getDownloadDao();
        //打开log
        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
    }

    public static DownloadDBHelper getInstance(Context context) {
        if (defaultInstance == null) {
            synchronized (DownloadDBHelper.class) {
                if (defaultInstance == null) {
                    defaultInstance = new DownloadDBHelper(context);
                }
            }
        }
        return defaultInstance;
    }

    //保存
    @Override
    public void saveDownload(Download download) {
        List<Download> list = queryDownload(download);
        if (list != null && list.size() > 0) {
            download.setId(list.get(0).getId());
        }
        downloadDao.insertOrReplace(download);
    }


    //删除
    @Override
    public void deleteDownload(Download download) {
        DeleteQuery<Download> qb = downloadDao.queryBuilder().where(
                DownloadDao.Properties.DownloadId.eq(download.getDownloadId())).buildDelete();
        qb.executeDeleteWithoutDetachingEntities();
        //删除任务
        deleteDownloadTask(download);
    }

    //更新
    @Override
    public void updateDownload(Download download) {
        List<Download> result = queryDownload(download);
        if (result != null && result.size() > 0) {
            download.setId(result.get(0).getId());
            downloadDao.update(download);
        }
    }

    //更新
    @Override
    public void updateDownloadFileLength(Download download) {
        List<Download> list = queryDownload(download);
        if (list != null && list.size() > 0) {
            for (Download d : list) {
                d.setFileLength(download.getFileLength());
                downloadDao.update(d);
            }
        }
    }

    //更新
    @Override
    public void updateDownloadDownloadSize(Download download) {
        List<Download> list = queryDownload(download);
        if (list != null && list.size() > 0) {
            for (Download d : list) {
                d.setDownloadSize(download.getDownloadSize());
                downloadDao.update(d);
            }
        }
    }

    @Override
    public List<Download> queryDownload(Download download) {
        Query<Download> query = downloadDao.queryBuilder().where(
                DownloadDao.Properties.DownloadId.eq(download.getDownloadId())
        ).build();
        List<Download> result = query.list();
        return result;
    }

    @Override
    public List<Download> queryAllDownload() {
        Query<Download> query = downloadDao.queryBuilder().build();
        return query.list();
    }


    //批量保存下载队列
    @Override
    public void saveDownloadTask(List<DownloadTask> taskList) {
        downloadTaskDao.insertInTx(taskList);
    }

    //删除
    @Override
    public void deleteDownloadTask(Download download) {
        DeleteQuery<DownloadTask> qb = downloadTaskDao.queryBuilder()
                .where(DownloadTaskDao.Properties.DownloadId.eq(download.getDownloadId())).buildDelete();
        qb.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public List<DownloadTask> queryDownloadTask(DownloadTask task) {
        Query<DownloadTask> qb = downloadTaskDao.queryBuilder()
                .where(DownloadTaskDao.Properties.DownloadId.eq(task.getDownloadId()),
                        DownloadTaskDao.Properties.DownloadTaskId.eq(task.getDownloadTaskId())
                ).build();
        return qb.list();
    }

    //保存下载队列
    @Override
    public synchronized void saveDownloadTask(DownloadTask task) {
        List<DownloadTask> list = queryDownloadTask(task);
        if (list != null && list.size() > 0) {
            task.setId(list.get(0).getId());
        }
        downloadTaskDao.insertOrReplace(task);
    }

    //更新下载队列
    @Override
    public void updateDownloadTask(DownloadTask task) {
        List<DownloadTask> list = queryDownloadTask(task);
        if (list != null && list.size() > 0) {
            for (DownloadTask downloadTask : list) {
                downloadTask.setDownloadSize(task.getDownloadSize());
                downloadTaskDao.update(downloadTask);
            }
        }
    }

    //删除下载队列
    @Override
    public void deleteDownloadTask(DownloadTask task) {
        DeleteQuery<DownloadTask> qb = downloadTaskDao.queryBuilder()
                .where(DownloadTaskDao.Properties.DownloadId.eq(task.getDownloadId()),
                        DownloadTaskDao.Properties.DownloadTaskId.eq(task.getDownloadTaskId())
                ).buildDelete();
        qb.executeDeleteWithoutDetachingEntities();
    }

    //查询所有
    @Override
    public List<DownloadTask> queryAllDownloadTask(Download download) {
        Query<DownloadTask> query = downloadTaskDao.queryBuilder()
                .where(DownloadTaskDao.Properties.DownloadId.eq(download.getDownloadId()))
                .build();
        return query.list();
    }


    //查询所有下载和下载任务
    @Override
    public List<Download> queryAllDownloadAndTask() {
        Query<Download> query = downloadDao.queryBuilder().build();
        List<Download> downloadList = query.list();
        if (downloadList != null && downloadList.size() > 0) {

            for (Download download : downloadList) {
                download.getDownloadTaskList().addAll(queryAllDownloadTask(download));
            }
        }
        return downloadList;
    }

}
