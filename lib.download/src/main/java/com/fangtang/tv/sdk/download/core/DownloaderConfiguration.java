package com.fangtang.tv.sdk.download.core;

import android.content.Context;

import com.fangtang.tv.sdk.download.core.assist.QueueProcessingType;
import com.fangtang.tv.sdk.download.core.cache.naming.FileNameGenerator;
import com.fangtang.tv.sdk.download.core.db.DownloadDBHelper;
import com.fangtang.tv.sdk.download.core.db.IDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.db.NoOpDownloadDBHelper;
import com.fangtang.tv.sdk.download.core.download.IDownloader;
import com.fangtang.tv.sdk.download.utils.DL;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * Configuration
 */
public final class DownloaderConfiguration {

    public final Context context;

    public final Executor taskExecutor;
    public final Executor taskExecutorForLoadVideoInfo;

    public final boolean customExecutor;
    public final boolean customExecutorForLoadVideoInfo;

    public final int threadPoolSize;
    public final int threadPriority;
    public final QueueProcessingType tasksProcessingType;

    public final IDownloader downloader;
    public final IDownloadDBHelper downloadDBHelper;
    public final File cacheDir;
    public final FileNameGenerator diskCacheFileNameGenerator;

    public final DownloadOptions defaultDownloadOptions;

    public final int maxDownloadingSize;
    public final boolean supportBreakPoint;

    private DownloaderConfiguration(final Builder builder) {
        context = builder.context;

        threadPoolSize = builder.threadPoolSize;
        threadPriority = builder.threadPriority;
        tasksProcessingType = builder.tasksProcessingType;

        taskExecutor = builder.taskExecutor;
        customExecutor = builder.customExecutor;

        taskExecutorForLoadVideoInfo = builder.taskExecutorForLoadVideoInfo;
        customExecutorForLoadVideoInfo = builder.customExecutorForLoadVideoInfo;

        downloader = builder.downloader;
        cacheDir = builder.cacheDir;
        diskCacheFileNameGenerator = builder.diskCacheFileNameGenerator;
        downloadDBHelper = builder.downloadDBHelper;
        defaultDownloadOptions = builder.defaultDownloadOptions;
        maxDownloadingSize = builder.maxDownloadingSize;
        supportBreakPoint = builder.supportBreakPoint;

        DL.writeDebugLogs(builder.writeLogs);
    }

    public static DownloaderConfiguration createDefault(Context context) {
        return new Builder(context).build();
    }

    public static class Builder {

        private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
                + "can overlap taskExecutor() and taskExecutorForLoadVideoInfo() calls.";

        public static final int DEFAULT_THREAD_POOL_SIZE = 10;
        public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
        public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

        public static final int DEFAULT_MAX_DOWNLOADING_SIZE = 5;

        public int maxDownloadingSize = DEFAULT_MAX_DOWNLOADING_SIZE;

        private Executor taskExecutor = null;
        private boolean customExecutor = false;

        private Executor taskExecutorForLoadVideoInfo = null;
        private boolean customExecutorForLoadVideoInfo = false;

        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int threadPriority = DEFAULT_THREAD_PRIORITY;
        private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

        private FileNameGenerator diskCacheFileNameGenerator = null;
        private IDownloader downloader = null;

        private Context context;
        private boolean writeLogs = false;
        public boolean supportBreakPoint = false;

        private File cacheDir;

        public IDownloadDBHelper downloadDBHelper;

        private DownloadOptions defaultDownloadOptions = null;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        public DownloaderConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new DownloaderConfiguration(this);
        }

        public Builder taskExecutor(Executor executor) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                DL.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.taskExecutor = executor;
            return this;
        }

        public Builder taskExecutorForLoadVideoInfo(Executor executorForLoadVideoInfo) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                DL.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.taskExecutorForLoadVideoInfo = executorForLoadVideoInfo;
            return this;
        }

        public Builder threadPoolSize(int threadPoolSize) {
            if (taskExecutor != null) {
                DL.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder threadPriority(int threadPriority) {
            if (taskExecutor != null || taskExecutorForLoadVideoInfo != null) {
                DL.w(WARNING_OVERLAP_EXECUTOR);
            }
            if (threadPriority < Thread.MIN_PRIORITY) {
                this.threadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    this.threadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.threadPriority = threadPriority;
                }
            }
            return this;
        }

        public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
            if (taskExecutor != null || taskExecutorForLoadVideoInfo != null) {
                DL.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.tasksProcessingType = tasksProcessingType;
            return this;
        }

        public Builder videoDownloader(IDownloader downloader) {
            this.downloader = downloader;
            return this;
        }

        public Builder supportBreakPoint(boolean supportBreakPoint) {
            this.supportBreakPoint = supportBreakPoint;
            return this;
        }

        public Builder maxDownloadingSize(int maxDownloadingSize) {
            this.maxDownloadingSize = maxDownloadingSize;
            return this;
        }

        public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            this.diskCacheFileNameGenerator = fileNameGenerator;
            return this;
        }

        private void initEmptyFieldsWithDefaultValues() {

            if (taskExecutor == null) {
                taskExecutor = DefaultConfigurationFactory
                        .createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutor = true;
            }

            if (taskExecutorForLoadVideoInfo == null) {
                taskExecutorForLoadVideoInfo = DefaultConfigurationFactory
                        .createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutorForLoadVideoInfo = true;
            }

            //downloader
            if (downloader == null) {
                downloader = DefaultConfigurationFactory.createVideoDownloader(context.getApplicationContext());
            }

            if (cacheDir == null) {
                cacheDir = DefaultConfigurationFactory.createDiskCacheDir(context.getApplicationContext());
            }

            if (diskCacheFileNameGenerator == null) {
                diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
            }

            if (downloadDBHelper == null) {
                if(supportBreakPoint){
                    downloadDBHelper = DownloadDBHelper.getInstance(context);
                }else{
                    downloadDBHelper = NoOpDownloadDBHelper.getInstance();
                }
            }

            if (defaultDownloadOptions == null) {
                defaultDownloadOptions = DownloadOptions.createSimple();
            }
        }
    }
}
