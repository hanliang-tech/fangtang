package com.fangtang.tv.sdk.base.remote;

import android.app.Application;

import com.fangtang.tv.sdk.base.bean.StatusBean;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.net.KCallBack;
import com.fangtang.tv.sdk.base.remote.bean.Remote;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindCode;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo;
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindStatus;
import com.fangtang.tv.sdk.base.remote.server.impl.RemoteAPIImpl;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteManager implements IRemoteManager {

    private static final String TAG = RemoteManager.class.getSimpleName();

    private static RemoteManager instance;
    private RemoteAPIImpl remoteAPIImpl;

    private List<RemoteListener> remoteListenerList
            = Collections.synchronizedList(new ArrayList<RemoteListener>());

    private RemoteManager() {
    }

    public synchronized static RemoteManager getInstance() {
        if (instance == null) {
            synchronized (RemoteManager.class) {
                if (instance == null) {
                    instance = new RemoteManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Application application, DeviceManager deviceManager) {
        remoteAPIImpl = new RemoteAPIImpl(deviceManager);
    }

    @Override
    public void getBindDeviceCode(final RemoteDeviceBindCodeListener listener) {
        remoteAPIImpl.bindDeviceCode(new KCallBack<StatusBean<RemoteDeviceBindCode>>() {
            @Override
            public void onSuccess(@Nullable StatusBean<RemoteDeviceBindCode> bindCodeStatusBean) {
                if (listener != null) {
                    if (bindCodeStatusBean != null && bindCodeStatusBean.data != null) {
                        listener.onBindCodeSuccess(bindCodeStatusBean.data);
                    } else {
                        listener.onBindCodeError(new Exception("get bind device code error!"));
                    }
                }
            }

            @Override
            public void onFail(int code, @Nullable String msg) {
                if (listener != null) {
                    listener.onBindCodeError(new Exception(msg));
                }
            }
        });
    }

    @Override
    public void getBindDeviceInfo(final RemoteDeviceBindInfoListener listener) {
        remoteAPIImpl.getBindDeviceInfo(new KCallBack<StatusBean<RemoteDeviceBindInfo>>() {
            @Override
            public void onSuccess(@Nullable StatusBean<RemoteDeviceBindInfo> statusBean) {
                if (listener != null) {
                    if (statusBean != null && statusBean.data != null) {
                        listener.onBindInfoSuccess(statusBean.data);
                    } else {
                        listener.onBindInfoError(new Exception("get bind device info error!"));
                    }
                }
            }

            @Override
            public void onFail(int code, @Nullable String msg) {
                if (listener != null) {
                    listener.onBindInfoError(new Exception(msg));
                }
            }
        });
    }

    @Override
    public void unbindDeviceInfo(String deviceId, final RemoteDeviceUnBindListener listener) {
        remoteAPIImpl.unbindDeviceInfo(deviceId, new KCallBack<StatusBean<RemoteDeviceBindStatus>>() {
            @Override
            public void onSuccess(@Nullable StatusBean<RemoteDeviceBindStatus> statusStatusBean) {
                if (listener != null) {
                    if (statusStatusBean != null && statusStatusBean.data != null) {
                        listener.onUnBindDeviceSuccess(statusStatusBean.data);
                    } else {
                        listener.onUnBindDeviceError(new Exception("un bind device info error!"));
                    }
                }
            }

            @Override
            public void onFail(int code, @Nullable String msg) {
                if (listener != null) {
                    listener.onUnBindDeviceError(new Exception(msg));
                }
            }
        });
    }

    public interface RemoteListener {
        void onRemoteChanged(Remote remote);

        void onRemoteError(Exception e);
    }

    @Override
    public boolean addRemoteListener(RemoteListener listener) {
        if (remoteListenerList != null && !remoteListenerList.contains(listener)) {
            return remoteListenerList.add(listener);
        }
        return false;
    }

    @Override
    public boolean removeRemoteListener(RemoteListener listener) {
        return remoteListenerList != null && remoteListenerList.remove(listener);
    }

    private void notifyRemoteChanged(Remote remote) {
        if (remoteListenerList != null && remoteListenerList.size() > 0) {
            for (RemoteListener listener : remoteListenerList) {
                try {
                    if (listener != null) {
                        listener.onRemoteChanged(remote);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyRemoteError(Exception ex) {
        if (remoteListenerList != null && remoteListenerList.size() > 0) {
            for (RemoteListener listener : remoteListenerList) {
                try {
                    if (listener != null) {
                        listener.onRemoteError(ex);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void release() {
        if (remoteListenerList != null) {
            remoteListenerList.clear();
            remoteListenerList = null;
        }
    }
}
