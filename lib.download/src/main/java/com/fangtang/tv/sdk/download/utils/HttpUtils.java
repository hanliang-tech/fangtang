package com.fangtang.tv.sdk.download.utils;

import com.fangtang.tv.sdk.download.Constant;

/**
 * Http工具
 * Created by liulipeng on 15-9-8.
 */
public class HttpUtils {

    /**
     * 判断是否支持断点下载
     * @param rangesHeader
     * @return
     */
    public static boolean acceptRanges(String rangesHeader){
        return Constant.ACCEPT_RANGES_BYTES.equals(rangesHeader);
    }
}
