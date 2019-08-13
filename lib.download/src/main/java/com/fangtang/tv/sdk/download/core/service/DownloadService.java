package com.fangtang.tv.sdk.download.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.IDownloadListener;
import com.fangtang.tv.sdk.download.IDownloadService;
import com.fangtang.tv.sdk.download.utils.DL;

import java.util.List;

/**
 * DownloadService
 */
public class DownloadService extends Service {

    private DownloadServiceManager serviceManager;

    @Override
    public IBinder onBind(Intent intent) {
        DL.e("#------DownloadService---onBind--->>>>>>>");
        return mBinder;
    }

    @Override
    public void onCreate() {
        serviceManager = DownloadServiceManager.getInstance();
        DL.e("#------DownloadService---onCreate--->>>>>>>");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private IDownloadService.Stub mBinder = new IDownloadService.Stub() {

        @Override
        public void registerListener(IDownloadListener listener) throws RemoteException {
            DL.e("#------DownloadService---registerListener--->>>>>>>" + listener);
            serviceManager.registerListener(listener);
        }

        @Override
        public void unregisterListener(IDownloadListener listener) throws RemoteException {
            DL.e("#------DownloadService---unregisterListener--->>>>>>>" + listener);
            serviceManager.unregisterListener(listener);
        }

        @Override
        public void download(Download info) throws RemoteException {
            DL.e("#------DownloadService---download--->>>>>>>" + info);
            serviceManager.download(info);
        }

        @Override
        public void start(String taskId) throws RemoteException {
            DL.e("#------DownloadService---start--->>>>>>>" + taskId);
            serviceManager.start(taskId);
        }

        @Override
        public void startAll() throws RemoteException {
            serviceManager.startAll();
        }

        @Override
        public void pause(String taskId) throws RemoteException {
            serviceManager.pause(taskId);
        }

        @Override
        public void pauseAll() throws RemoteException {
            serviceManager.pauseAll();
        }


        @Override
        public void delete(String downloadId) throws RemoteException {
            serviceManager.delete(downloadId);
        }

        @Override
        public void deleteAll() throws RemoteException {
            serviceManager.deleteAll();
        }

        @Override
        public Download getDownload(String downloadId) {
            return serviceManager.getDownload(downloadId);
        }

        @Override
        public List<Download> getDownloadList() throws RemoteException {
            return serviceManager.getDownloadList();
        }
    };


    @Override
    public void onDestroy() {
        DL.e("#------DownloadService---onDestroy--->>>>>>>");
        serviceManager.onDestroy();
        super.onDestroy();
    }
}
