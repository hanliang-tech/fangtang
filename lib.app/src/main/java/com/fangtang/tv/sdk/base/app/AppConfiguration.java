package com.fangtang.tv.sdk.base.app;

import android.content.Context;

import com.fangtang.tv.sdk.base.app.install.AndroidAppInstallManager;
import com.fangtang.tv.sdk.base.app.install.AppInstallConfiguration;
import com.fangtang.tv.sdk.base.app.install.IInstallManager;
import com.fangtang.tv.sdk.base.core.ExecutorSupplier;
import com.fangtang.tv.sdk.base.device.DeviceManager;

public class AppConfiguration {

    public final Context context;
    public final DeviceManager deviceManager;
    public final ExecutorSupplier executorSupplier;
    public final IInstallManager appInstallManager;
    public final boolean reportInstalledApp;

    AppConfiguration(final Builder builder) {
        this.context = builder.context;
        this.deviceManager = builder.deviceManager;
        this.executorSupplier = builder.executorSupplier;
        this.appInstallManager = builder.appInstallManager;
        this.reportInstalledApp = builder.reportInstalledApp;
    }

    public static final class Builder {

        private Context context;
        private DeviceManager deviceManager;
        private ExecutorSupplier executorSupplier;
        private IInstallManager appInstallManager;
        private boolean reportInstalledApp = true;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public AppConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new AppConfiguration(this);
        }

        public Builder reportInstalledApp(boolean reportInstalledApp) {
            this.reportInstalledApp = reportInstalledApp;
            return this;
        }

        public Builder setDeviceManager(DeviceManager deviceManager) {
            this.deviceManager = deviceManager;
            return this;
        }

        public Builder setExecutorSupplier(ExecutorSupplier executorSupplier) {
            this.executorSupplier = executorSupplier;
            return this;
        }

        public Builder setInstallManager(IInstallManager appInstallManager) {
            this.appInstallManager = appInstallManager;
            return this;
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (appInstallManager == null) {
                //默认的安装器
                AndroidAppInstallManager installer = AndroidAppInstallManager.getInstance();
                AppInstallConfiguration configuration = new AppInstallConfiguration
                        .Builder(context).build();
                installer.init(configuration);

                appInstallManager = installer;
            }
        }
    }
}
