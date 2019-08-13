package com.fangtang.tv.sdk.download.core.download;

import com.fangtang.tv.sdk.download.DownloadTask;
import com.fangtang.tv.sdk.download.core.DefaultConfigurationFactory;
import com.fangtang.tv.sdk.download.core.DownloaderConfiguration;
import com.fangtang.tv.sdk.download.core.task.BaseDownloadTask;
import com.fangtang.tv.sdk.download.core.task.VideoDownloadTask;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


public class BaseDownloadEngine {

    public final DownloaderConfiguration configuration;

    protected Executor taskExecutor;
    protected Executor taskExecutorForCachedImages;
    protected Executor taskDistributor;

    protected final AtomicBoolean isPaused = new AtomicBoolean();

    BaseDownloadEngine(DownloaderConfiguration configuration) {
        this.configuration = configuration;

        taskExecutor = configuration.taskExecutor;
        taskExecutorForCachedImages = configuration.taskExecutorForLoadVideoInfo;

        taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
    }

    void submit(VideoDownloadTask task) {
        initExecutorsIfNeed();
        taskExecutor.execute(task);
    }

    void submit(BaseDownloadTask task) {
        initExecutorsIfNeed();
        taskExecutor.execute(task);
    }

    private void initExecutorsIfNeed() {
        if (!configuration.customExecutor && ((ExecutorService) taskExecutor).isShutdown()) {
            taskExecutor = createTaskExecutor();
        }
        if (!configuration.customExecutorForLoadVideoInfo && ((ExecutorService) taskExecutorForCachedImages)
                .isShutdown()) {
            taskExecutorForCachedImages = createTaskExecutor();
        }
    }

    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory
                .createExecutor(configuration.threadPoolSize, configuration.threadPriority,
                        configuration.tasksProcessingType);
    }

    /**
     * Stops engine, cancels all running and scheduled download video tasks. Clears internal data.
     */
    void stop() {
        if (!configuration.customExecutor) {
            ((ExecutorService) taskExecutor).shutdownNow();
        }
        if (!configuration.customExecutorForLoadVideoInfo) {
            ((ExecutorService) taskExecutorForCachedImages).shutdownNow();
        }
    }

    public boolean isPaused() {
        boolean cancelled = isPaused.get();
        return cancelled;
    }

    public boolean pause() {
        isPaused.set(true);
        return isPaused();
    }

    /**
     * 更新文件的下载的长度
     *
     * @param downloadSize
     */
    public  void updateDownloadSize(long downloadSize) {

    }

    /**
     * 更新文件的总长度
     *
     * @param fileLength
     */
    public synchronized void updateFileLength(long fileLength) {

    }

    /**
     * 更新下载任务的状态
     * @param downloadTask
     */
    public synchronized void updateDownloadTaskStatus(DownloadTask downloadTask) {

    }
}
