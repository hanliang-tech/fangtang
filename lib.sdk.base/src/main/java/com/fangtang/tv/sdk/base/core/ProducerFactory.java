
package com.fangtang.tv.sdk.base.core;

public class ProducerFactory {

    private final ExecutorSupplier mExecutorSupplier;

    public ProducerFactory(
            ExecutorSupplier executorSupplier) {
        mExecutorSupplier = executorSupplier;
    }

//    public LocalResourceFetchProducer newLocalResourceFetchProducer() {
//        return new LocalResourceFetchProducer(
//                mExecutorSupplier.forLocalStorageRead(),
//                mPooledByteBufferFactory,
//                mResources);
//    }
}
