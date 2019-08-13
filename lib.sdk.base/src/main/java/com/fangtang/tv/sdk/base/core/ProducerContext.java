
package com.fangtang.tv.sdk.base.core;

public interface ProducerContext {

    String getId();

    ProducerListener getListener();

    Object getCallerContext();

    Priority getPriority();

    boolean isIntermediateResultExpected();

    void addCallbacks(ProducerContextCallbacks callbacks);
}
