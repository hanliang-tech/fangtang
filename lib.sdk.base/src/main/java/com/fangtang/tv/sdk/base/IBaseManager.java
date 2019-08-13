package com.fangtang.tv.sdk.base;

import android.app.Application;

public interface IBaseManager {

    void init(Application application);
    void release();
}
