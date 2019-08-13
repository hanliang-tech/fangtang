package com.fangtang.tv.sdk.base.nlp;

import android.content.Context;

import com.fangtang.tv.sdk.base.device.DeviceManager;
import com.fangtang.tv.sdk.base.push.IPushManager;
import com.fangtang.tv.sdk.base.router.IRouterManager;

public class NLPConfiguration {

    public final Context context;
    public final DeviceManager deviceManager;
    public final IPushManager pushManager;
    public final IRouterManager routerManager;


    NLPConfiguration(final Builder builder) {
        this.context = builder.context;
        this.deviceManager = builder.deviceManager;
        this.pushManager = builder.pushManager;
        this.routerManager = builder.routerManager;
    }

    public static final class Builder {

        private Context context;

        private DeviceManager deviceManager;
        private IPushManager pushManager;
        private IRouterManager routerManager;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setDeviceManager(DeviceManager deviceManager) {
            this.deviceManager = deviceManager;
            return this;
        }

        public Builder setPushManager(IPushManager pushManager) {
            this.pushManager = pushManager;
            return this;
        }

        public Builder setRouterManager(IRouterManager routerManager) {
            this.routerManager = routerManager;
            return this;
        }

        public NLPConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new NLPConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

            if (deviceManager == null) {
                throw new IllegalArgumentException("deviceManager must not be null...");
            }

            if (pushManager == null) {
                throw new IllegalArgumentException("pushManager must not be null...");
            }

            if (routerManager == null) {
                throw new IllegalArgumentException("routerManager must not be null...");
            }
        }
    }
}
