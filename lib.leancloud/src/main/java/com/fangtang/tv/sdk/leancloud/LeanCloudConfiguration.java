package com.fangtang.tv.sdk.leancloud;

import android.content.Context;

import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.kv.IKVManager;
import com.fangtang.tv.sdk.base.remote.IRemoteManager;

public class LeanCloudConfiguration {

    public final String appKey;
    public final String clientKey;

    public final IKVManager kvManager;
    public final DeviceManager deviceManager;
    public final IRemoteManager remoteManager;

    public final boolean writeLogs;

    public final Context context;

    LeanCloudConfiguration(final Builder builder) {
        this.context = builder.context;
        this.kvManager = builder.kvManager;
        this.deviceManager = builder.deviceManager;
        this.remoteManager = builder.remoteManager;
        this.writeLogs = builder.writeLogs;

        this.appKey = builder.appKey;
        this.clientKey = builder.clientKey;
    }

    public static final class Builder {

        private Context context;

        private String appKey;
        private String clientKey;

        private IKVManager kvManager;
        private DeviceManager deviceManager;
        private IRemoteManager remoteManager;

        private boolean writeLogs = false;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setAppKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        public Builder setClientKey(String clientKey) {
            this.clientKey = clientKey;
            return this;
        }

        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        public Builder setKVManager(IKVManager manager){
            this.kvManager = manager;
            return this;
        }

        public Builder setDeviceManager(DeviceManager deviceManager) {
            this.deviceManager = deviceManager;
            return this;
        }

        public Builder setRemoteManager(IRemoteManager remoteManager) {
            this.remoteManager = remoteManager;
            return this;
        }

        public LeanCloudConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new LeanCloudConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

            if (appKey == null) {
                throw new IllegalArgumentException("appKey must not be null...");
            }

            if (clientKey == null) {
                throw new IllegalArgumentException("clientKey must not be null...");
            }

            if (deviceManager == null) {
                throw new IllegalArgumentException("deviceManager must not be null...");
            }

            if (remoteManager == null) {
                throw new IllegalArgumentException("RemoteManager must not be null...");
            }
        }
    }
}
