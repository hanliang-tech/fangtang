package com.fangtang.tv.sdk;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;
import com.fangtang.tv.sdk.base.app.IAppManager;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.kv.IKVManager;
import com.fangtang.tv.sdk.base.nlp.INLPManager;
import com.fangtang.tv.sdk.base.push.IPushManager;
import com.fangtang.tv.sdk.base.remote.IRemoteManager;
import com.fangtang.tv.sdk.base.remote.RemoteManager;
import com.fangtang.tv.sdk.base.router.IRouterManager;
import com.fangtang.tv.sdk.base.scanner.IScannerManager;

public final class FangTang {

    private static final String ERROR_INIT_CONFIG_WITH_NULL = "FangTang configuration can not be initialized with null";

    private FangTangConfiguration configuration;

    public IKVManager kvManager;

    public INLPManager nlpManager;
    public IRouterManager routerManager;
    public IPushManager pushManager;
    public IAppManager appManager;
    public IScannerManager scannerManager;
    public IRemoteManager remoteManager;
    public DeviceManager deviceManager;

    public boolean writeLogs;
    public Context applicationContext;

    private volatile static FangTang instance;

    public static FangTang getInstance() {
        if (instance == null) {
            synchronized (FangTang.class) {
                if (instance == null) {
                    instance = new FangTang();
                }
            }
        }
        return instance;
    }

    private FangTang() {
    }

    public synchronized void init(FangTangConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null) {
            this.configuration = configuration;
        }

        this.applicationContext = configuration.context;

        this.kvManager = configuration.KVManager;

        this.nlpManager = configuration.nlpManager;
        this.routerManager = configuration.routerManager;
        this.pushManager = configuration.pushManager;
        this.appManager = configuration.appManager;
        this.scannerManager = configuration.scannerManager;
        this.deviceManager = configuration.deviceManager;

        this.writeLogs = configuration.writeLogs;

        this.remoteManager = RemoteManager.getInstance();

        init((Application) configuration.context);
    }

    private void init(Application application) {
        routerManager.init(application);
        remoteManager.init(application, deviceManager);
        Utils.init(application);
    }

    public void destroy() {
        configuration = null;
        nlpManager.release();
        routerManager.release();
        pushManager.release();
    }
}
