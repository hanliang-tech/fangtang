package com.fangtang.tv.sdk.download;

/**
 * 常量
 * Created by liulipeng on 15-8-26.
 */
public class Constant {

    //accept-ranges
    public static final String ACCEPT_RANGES_NONE = "none";
    public static final String ACCEPT_RANGES_BYTES = "bytes";

    //下载的状态
    public final static int DOWNLOAD_STATE_INIT = -1;//下载初始化
    public final static int DOWNLOAD_STATE_INIT_ERROR = 0;//下载初始化失败
    public final static int DOWNLOAD_STATE_VIDEO_INIT_ERROR = 1;//获取视频信息失败
    public final static int DOWNLOAD_STATE_VIDEO_INIT = 2;//获取视频信息成功
    public final static int DOWNLOAD_STATE_DOWNLOADING = 3;//下载中
    public final static int DOWNLOAD_STATE_PAUSE = 4;//下载暂停
    public final static int DOWNLOAD_STATE_FINISH = 5;//下载结束
    public final static int DOWNLOAD_STATE_EXCEPTION = 6;//下载异常
    public final static int DOWNLOAD_STATE_CANCEL = 7;//下载取消

    //下载任务的状态
    public final static int TASK_STATE_INIT = -1;//下载初始化
    public final static int TASK_STATE_DOWNLOADING = 1;//下载中
    public final static int TASK_STATE_FINISH = 2;//下载结束
    public final static int TASK_STATE_EXCEPTION = 3;//下载异常
    public final static int TASK_STATE_PAUSE = 4;//下载暂停

    public final static int VIDEO_TYPE_NORMAL = 1;//普通视频可以下载
    public final static int VIDEO_TYPE_RANGE = 2;//普通视频可以下载

    public final static int EXCEPTION_NO_SDCARD = 1;// 无SD卡
    public final static int EXCEPTION_NO_NETWORK = 2;// 无网络
    public final static int EXCEPTION_NO_SPACE = 3;// 无空间
    public final static int EXCEPTION_NO_COPYRIGHT = 4;// 无版权
    public final static int EXCEPTION_NO_RESOURCES = 5;// 无资源
    public final static int EXCEPTION_HTTP_NOT_FOUND = 6;// 网络404错误
    public final static int EXCEPTION_TIMEOUT = 7;// 网络超时
    public final static int EXCEPTION_WRITE_ERROR = 8;// 写入info文件写入错误
    public final static int EXCEPTION_UNKNOWN_ERROR = 9;// 未知错误


}
