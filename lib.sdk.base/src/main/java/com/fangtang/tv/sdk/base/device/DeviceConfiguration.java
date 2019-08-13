package com.fangtang.tv.sdk.base.device;

import android.content.Context;

import com.fangtang.tv.sdk.base.uuid.IUUIDManager;

public class DeviceConfiguration {

    public final String appId;
    public final String appKey;
    public final String channel;

    public final IUUIDManager UUIDManager;

    public final Context context;

    DeviceConfiguration(final Builder builder) {
        this.context = builder.context;

        this.appId = builder.appId;
        this.appKey = builder.appKey;
        this.channel = builder.channel;

        this.UUIDManager = builder.UUIDManager;
    }

    public static final class Builder {

        private Context context;

        private String appId;
        private String appKey;
        private String channel;

        private IUUIDManager UUIDManager;

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

        public Builder setUUIDManager(IUUIDManager UUIDManager) {
            this.UUIDManager = UUIDManager;
            return this;
        }

        public DeviceConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new DeviceConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

            if (appId == null) {
                throw new IllegalArgumentException("appId must not be null...");
            }

            if (channel == null) {
                throw new IllegalArgumentException("clientKey must not be null...");
            }

            if (UUIDManager == null) {
                throw new IllegalArgumentException("UUIDManager must not be null...");
            }
        }
    }
}
