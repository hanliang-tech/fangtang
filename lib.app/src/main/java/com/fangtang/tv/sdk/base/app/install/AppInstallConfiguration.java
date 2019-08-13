package com.fangtang.tv.sdk.base.app.install;

import android.content.Context;

public class AppInstallConfiguration {

    public final Context context;

    AppInstallConfiguration(final Builder builder) {
        this.context = builder.context;
    }

    public static final class Builder {

        private Context context;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public AppInstallConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new AppInstallConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

        }
    }
}
