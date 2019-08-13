package com.fangtang.tv.sdk.base.remote;


import android.app.Application;

import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindStatus;

import org.jetbrains.annotations.NotNull;

public interface IRemoteManager {


    void getBindDeviceInfo(RemoteDeviceBindInfoListener listener);

    void init(Application application, DeviceManager deviceManager);

    boolean addRemoteListener(@NotNull RemoteManager.RemoteListener leanCloudManager);

    boolean removeRemoteListener(@NotNull RemoteManager.RemoteListener leanCloudManager);

    interface RemoteDeviceBindInfoListener {
        void onBindInfoSuccess(RemoteDeviceBindInfo bindInfo);

        void onBindInfoError(Exception e);
    }

    void getBindDeviceCode(RemoteDeviceBindCodeListener listener);

    interface RemoteDeviceBindCodeListener {
        void onBindCodeSuccess(RemoteDeviceBindCode bindCode);

        void onBindCodeError(Exception e);
    }


    void unbindDeviceInfo(String deviceId, RemoteDeviceUnBindListener listener);

    interface RemoteDeviceUnBindListener {
        void onUnBindDeviceSuccess(RemoteDeviceBindStatus status);

        void onUnBindDeviceError(Exception e);
    }
}
