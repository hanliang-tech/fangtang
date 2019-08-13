package com.fangtang.tv.sdk.ui.widget;

import com.fangtang.tv.support.item2.widget.IWidget;

public interface IOriginWidget extends IWidget {
    //必须唯一名称
    String NAME = "APk_Widget";

    public void setApkUrl(int parentWidth, int parentHeight, String apkUrl);

    public void setApkName(int parentWidth, int parentHeight, String apkName);

    public void clearBitmap();
}
