/**
 * ****************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package com.fangtang.tv.sdk.download.core;

import android.os.Handler;

/**
 * Contains options for download task
 */
public final class DownloadOptions {

    private boolean supportMultiTask;
    private int downloadMaxTaskSize;

    private final Handler handler;
    private final boolean isSyncLoading;

    private DownloadOptions(Builder builder) {
        supportMultiTask = builder.supportMultiTask;
        downloadMaxTaskSize = builder.downloadMaxTaskSize;
        handler = builder.handler;
        isSyncLoading = builder.isSyncLoading;
    }

    public boolean supportMultiTask() {
        return supportMultiTask;
    }

    public int getDownloadTaskSize() {
        return downloadMaxTaskSize;
    }


    public Handler getHandler() {
        return handler;
    }

    boolean isSyncLoading() {
        return isSyncLoading;
    }

    public static class Builder {

        private static final int DEFAULT_MAX_DOWNLOAD_TASK_SIZE = 5;

        private boolean supportMultiTask;
        private int downloadMaxTaskSize = DEFAULT_MAX_DOWNLOAD_TASK_SIZE;
        private Handler handler = null;
        private boolean isSyncLoading = false;

        public Builder supportMultiTask(boolean supportMultiTask) {
            supportMultiTask = supportMultiTask;
            return this;
        }

        public Builder setMaxDownloadTaskSize(int downloadTaskSize) {
            this.downloadMaxTaskSize = downloadTaskSize;
            return this;
        }

        Builder syncLoading(boolean isSyncLoading) {
            this.isSyncLoading = isSyncLoading;
            return this;
        }

        public Builder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public DownloadOptions build() {
            return new DownloadOptions(this);
        }
    }


    public static DownloadOptions createSimple() {
        return new Builder()
                .setMaxDownloadTaskSize(3)
                .build();
    }
}
