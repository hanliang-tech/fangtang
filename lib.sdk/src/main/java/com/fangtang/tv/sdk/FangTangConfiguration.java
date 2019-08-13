package com.fangtang.tv.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fangtang.tv.sdk.base.app.IAppManager;
import com.fangtang.tv.sdk.base.core.DefaultExecutorSupplier;
import com.fangtang.tv.sdk.base.core.ExecutorSupplier;
import com.fangtang.tv.sdk.base.device.DeviceConfiguration;
import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.kv.DefaultKVManager;
import com.fangtang.tv.sdk.base.kv.IKVManager;
import com.fangtang.tv.sdk.base.nlp.INLPManager;
import com.fangtang.tv.sdk.base.nlp.NLPConfiguration;
import com.fangtang.tv.sdk.base.nlp.NLPManager;
import com.fangtang.tv.sdk.base.push.IPushManager;
import com.fangtang.tv.sdk.base.remote.IRemoteManager;
import com.fangtang.tv.sdk.base.remote.RemoteManager;
import com.fangtang.tv.sdk.base.router.IRouterManager;
import com.fangtang.tv.sdk.base.router.RouterManager;
import com.fangtang.tv.sdk.base.scanner.IScannerManager;
import com.fangtang.tv.sdk.base.scanner.ScanConfiguration;
import com.fangtang.tv.sdk.base.scanner.ScannerManager;
import com.fangtang.tv.sdk.base.uuid.DefaultUUIDManager;
import com.fangtang.tv.sdk.base.uuid.IUUIDManager;
import com.fangtang.tv.sdk.leancloud.LeanCloudConfiguration;
import com.fangtang.tv.sdk.leancloud.LeanCloudManager;

public class FangTangConfiguration {

    public final String appId;
    public final String appKey;
    public final String channel;
    public final String pushAppKey;
    public final String pushClientKey;

    public final IKVManager KVManager;
    public final IUUIDManager UUIDManager;

    public final INLPManager nlpManager;
    public final IRouterManager routerManager;
    public final IPushManager pushManager;
    public final IAppManager appManager;
    public final IScannerManager scannerManager;
    public final DeviceManager deviceManager;
    public final IRemoteManager remoteManager;

    public final boolean writeLogs;
    public final ExecutorSupplier mExecutorSupplier;

    public final Context context;

    private static final String KEY_APP_KEY = "FT_SDK_KEY";
    private static final String KEY_APP_SECRET = "FT_SDK_SECRET";

    FangTangConfiguration(final Builder builder) {
        this.context = builder.context;
        this.appId = builder.appId;
        this.appKey = builder.appKey;
        this.channel = builder.channel;

        this.pushAppKey = builder.pushAppKey;
        this.pushClientKey = builder.pushClientKey;

        this.KVManager = builder.kvManager;
        this.UUIDManager = builder.UUIDManager;

        this.nlpManager = builder.nlpManager;
        this.routerManager = builder.routerManager;
        this.pushManager = builder.pushManager;
        this.appManager = builder.appManager;
        this.scannerManager = builder.scannerManager;
        this.deviceManager = builder.deviceManager;
        this.remoteManager = builder.remoteManager;

        this.mExecutorSupplier = builder.mExecutorSupplier;

        this.writeLogs = builder.writeLogs;
    }

    public static final class Builder {

        public static final int DEFAULT_MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();

        private Context context;
        private String appId;
        private String appKey;
        private String channel;
        private String pushAppKey;
        private String pushClientKey;

        private IKVManager kvManager;
        private IUUIDManager UUIDManager;

        private INLPManager nlpManager;
        private IRouterManager routerManager;
        private IPushManager pushManager;
        private IAppManager appManager;
        private IScannerManager scannerManager;
        private DeviceManager deviceManager;
        private IRemoteManager remoteManager;

        private ExecutorSupplier mExecutorSupplier;

        private boolean writeLogs = false;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setChannel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder setPushAppKey(String pushAppKey) {
            this.pushAppKey = pushAppKey;
            return this;
        }

        public Builder setPushClientKey(String pushClientKey) {
            this.pushClientKey = pushClientKey;
            return this;
        }

        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        public Builder setKVManager(IKVManager KVManager) {
            this.kvManager = KVManager;
            return this;
        }

        public Builder setUUIDManager(IUUIDManager UUIDManager) {
            this.UUIDManager = UUIDManager;
            return this;
        }

        public Builder setPushManager(IPushManager pushManager) {
            this.pushManager = pushManager;
            return this;
        }

        public Builder setRemoteManager(IRemoteManager remoteManager) {
            this.remoteManager = remoteManager;
            return this;
        }

        public Builder setNLPManager(INLPManager nlpManager) {
            this.nlpManager = nlpManager;
            return this;
        }

        public Builder setAppManager(IAppManager appManager) {
            this.appManager = appManager;
            return this;
        }

        public Builder setRouterManager(IRouterManager routerManager) {
            this.routerManager = routerManager;
            return this;
        }

        public Builder setScannerManager(IScannerManager scannerManager) {
            this.scannerManager = scannerManager;
            return this;
        }

        public FangTangConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new FangTangConfiguration(this);
        }

        public Builder setExecutorSupplier(ExecutorSupplier executorSupplier) {
            mExecutorSupplier = executorSupplier;
            return this;
        }

        private void initEmptyFieldsWithDefaultValues() {

            if (TextUtils.isEmpty(appId)) {
                appId = readManifestValue(KEY_APP_KEY);
                if (TextUtils.isEmpty(appId)) {
                    throw new IllegalArgumentException("appId must not be null...");
                }
            }

            if (TextUtils.isEmpty(appKey)) {
                appKey = readManifestValue(KEY_APP_SECRET);
                if (TextUtils.isEmpty(appKey)) {
                    throw new IllegalArgumentException("appSecret must not be null...");
                }
            }

            if (TextUtils.isEmpty(channel)) {
                throw new IllegalArgumentException("channel must not be null...");
            }

            if (TextUtils.isEmpty(pushAppKey)) {
                throw new IllegalArgumentException("pushAppKey must not be null...");
            }

            if (TextUtils.isEmpty(pushClientKey)) {
                throw new IllegalArgumentException("pushClientKey must not be null...");
            }

            if (kvManager == null) {
                kvManager = new DefaultKVManager(context);
            }

            if (UUIDManager == null) {
                UUIDManager = new DefaultUUIDManager(context, kvManager);
            }

            if (appManager == null) {
                throw new IllegalArgumentException("AppManager must not be null...");
            }

            if (deviceManager == null) {
                deviceManager = DeviceManager.getInstance();
                DeviceConfiguration configuration = new DeviceConfiguration
                        .Builder(context)
                        .setAppId(appId)
                        .setAppKey(appKey)
                        .setChannel(channel)
                        .setUUIDManager(UUIDManager)
                        .build();
                deviceManager.init(configuration);
            }

            if (routerManager == null) {
                routerManager = new RouterManager(appManager);
            }


            if (remoteManager == null) {
                remoteManager = RemoteManager.getInstance();
            }

            if (pushManager == null) {
                LeanCloudManager leanCloudManager = new LeanCloudManager();
                //初始化push
                LeanCloudConfiguration pushConfiguration = new LeanCloudConfiguration.Builder(context)
                        .setKVManager(kvManager)
                        .setRemoteManager(remoteManager)
                        .setDeviceManager(deviceManager)
                        .setAppKey(pushAppKey)
                        .setClientKey(pushClientKey)
                        .build();
                leanCloudManager.init(pushConfiguration);

                pushManager = leanCloudManager;
            }

            if (nlpManager == null) {
                NLPManager manager = new NLPManager();
                NLPConfiguration nlpConfiguration = new NLPConfiguration.Builder(context)
                        .setPushManager(pushManager)
                        .setRouterManager(routerManager)
                        .setDeviceManager(deviceManager)
                        .build();
                manager.init(nlpConfiguration);
                nlpManager = manager;
            }

            if (scannerManager == null) {
                ScannerManager s = new ScannerManager();
                //scanner
                ScanConfiguration scanConfiguration = new ScanConfiguration
                        .Builder(context).build();
                s.init(scanConfiguration, kvManager, deviceManager, remoteManager);
                scannerManager = s;
            }

            if (mExecutorSupplier == null) {
                mExecutorSupplier = new DefaultExecutorSupplier(DEFAULT_MAX_NUM_THREADS);
            }
        }

        private String readManifestValue(String key) {
            String value = null;
            try {
                ApplicationInfo info = this.context.getPackageManager().getApplicationInfo(this.context.getPackageName(), PackageManager.GET_META_DATA);
                value = info.metaData.getString(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }
    }
}
