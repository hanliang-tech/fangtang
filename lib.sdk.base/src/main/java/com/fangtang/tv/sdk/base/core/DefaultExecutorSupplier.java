package com.fangtang.tv.sdk.base.core;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultExecutorSupplier implements ExecutorSupplier {

    private static final int NUM_IO_BOUND_THREADS = 2;
    private static final int NUM_LIGHTWEIGHT_BACKGROUND_THREADS = 1;

    private final Executor mIoBoundExecutor;
    private final Executor mDecodeExecutor;
    private final Executor mBackgroundExecutor;
    private final Executor mLightWeightBackgroundExecutor;

    public DefaultExecutorSupplier(int numCpuBoundThreads) {
        mIoBoundExecutor =
                Executors.newFixedThreadPool(
                        NUM_IO_BOUND_THREADS,
                        new PriorityThreadFactory(
                                Process.THREAD_PRIORITY_BACKGROUND, "FangTangIoBoundExecutor", true));
        mDecodeExecutor =
                Executors.newFixedThreadPool(
                        numCpuBoundThreads,
                        new PriorityThreadFactory(
                                Process.THREAD_PRIORITY_BACKGROUND, "FangTangDecodeExecutor", true));
        mBackgroundExecutor =
                Executors.newFixedThreadPool(
                        numCpuBoundThreads,
                        new PriorityThreadFactory(
                                Process.THREAD_PRIORITY_BACKGROUND, "FangTangBackgroundExecutor", true));
        mLightWeightBackgroundExecutor =
                Executors.newFixedThreadPool(
                        NUM_LIGHTWEIGHT_BACKGROUND_THREADS,
                        new PriorityThreadFactory(
                                Process.THREAD_PRIORITY_BACKGROUND, "FangTangLightWeightBackgroundExecutor", true));

    }

    @Override
    public Executor forLocalStorageRead() {
        return mIoBoundExecutor;
    }

    @Override
    public Executor forLocalStorageWrite() {
        return mIoBoundExecutor;
    }

    @Override
    public Executor forDecode() {
        return mDecodeExecutor;
    }

    @Override
    public Executor forBackgroundTasks() {
        return mBackgroundExecutor;
    }

    @Override
    public Executor forLightweightBackgroundTasks() {
        return mLightWeightBackgroundExecutor;
    }
}
