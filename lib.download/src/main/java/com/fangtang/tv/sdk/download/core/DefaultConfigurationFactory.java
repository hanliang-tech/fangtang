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

import android.content.Context;

import com.fangtang.tv.sdk.download.core.assist.QueueProcessingType;
import com.fangtang.tv.sdk.download.core.assist.deque.LIFOLinkedBlockingDeque;
import com.fangtang.tv.sdk.download.core.cache.naming.FileNameGenerator;
import com.fangtang.tv.sdk.download.core.cache.naming.NoOpFileNameGenerator;
import com.fangtang.tv.sdk.download.core.download.BaseDownloader;
import com.fangtang.tv.sdk.download.core.storage.StorageUtils;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class DefaultConfigurationFactory {

    public static Executor createExecutor(int threadPoolSize, int threadPriority,
                                          QueueProcessingType tasksProcessingType) {
        boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
        BlockingQueue<Runnable> taskQueue =
                lifo ? new LIFOLinkedBlockingDeque<Runnable>() : new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(threadPriority, "downloader-pool-"));
    }

    public static Executor createTaskDistributor() {
        return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY, "downloader-pool-d-"));
    }

    public static FileNameGenerator createFileNameGenerator() {
        return new NoOpFileNameGenerator();
    }

    public static File createDiskCacheDir(Context context) {
        File cacheDir = StorageUtils.getCacheDirectory(context, true);
        return cacheDir;
    }

    public static BaseDownloader createVideoDownloader(Context context) {
        return new BaseDownloader(context);
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
