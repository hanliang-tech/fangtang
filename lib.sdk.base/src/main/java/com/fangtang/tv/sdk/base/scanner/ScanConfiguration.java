package com.fangtang.tv.sdk.base.scanner;

import android.content.Context;

public class ScanConfiguration {

    public final Context context;

    ScanConfiguration(final Builder builder) {
        this.context = builder.context;
    }

    public static final class Builder {

        private Context context;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public ScanConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new ScanConfiguration(this);
        }

        private void initEmptyFieldsWithDefaultValues() {

        }
    }
}
