package com.fangtang.tv.sdk.download;


import android.os.Parcel;
import android.os.Parcelable;

import com.fangtang.tv.sdk.download.core.DownloadOptions;
import com.fangtang.tv.sdk.download.core.download.DownloadEngine;
import com.fangtang.tv.sdk.download.core.task.BaseDownloadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entity mapped to table "download".
 */
public class Download implements Parcelable {

    private Long id;
    private String downloadId;
    private String url;
    private String md5;
    private String path;
    private long fileLength;
    private int type;
    private int status = Constant.DOWNLOAD_STATE_INIT;
    private transient long lastRefreshTime;
    //不保存进数据库的字段
    private long downloadSize;
    private float progress;


    private List<DownloadTask> downloadTaskList = Collections.synchronizedList(new ArrayList<DownloadTask>());

    public List<BaseDownloadTask> downloadTaskRunableList = Collections.synchronizedList(new ArrayList<BaseDownloadTask>());

    public Download() {
    }

    public Download(Long id) {
        this.id = id;
    }

    public Download(Long id,
                    String downloadId,
                    String url,
                    String md5,
                    String path,
                    Long fileLength,
                    Integer type,
                    Long downloadSize,
                    Integer status) {
        this.id = id;
        this.downloadId = downloadId;
        this.url = url;
        this.md5 = md5;
        this.path = path;
        this.fileLength = fileLength;
        this.type = type;
        this.downloadSize = downloadSize;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public List<BaseDownloadTask> getDownloadTaskRunableList() {
        return downloadTaskRunableList;
    }

    public void setDownloadTaskRunableList(List<BaseDownloadTask> downloadTaskRunableList) {
        this.downloadTaskRunableList = downloadTaskRunableList;
    }

    public DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }

    public void setDownloadOptions(DownloadOptions downloadOptions) {
        this.downloadOptions = downloadOptions;
    }


    public Download(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void addDownloadTask(DownloadTask downloadTask) {
        downloadTaskList.add(downloadTask);
    }

    public List<DownloadTask> getDownloadTaskList() {
        return downloadTaskList;
    }

    public void setDownloadTaskList(List<DownloadTask> downloadTaskList) {
        this.downloadTaskList = downloadTaskList;
    }

    private DownloadEngine downloadEngine;

    public DownloadEngine getDownloadEngine() {
        return downloadEngine;
    }

    public void setDownloadEngine(DownloadEngine downloadEngine) {
        this.downloadEngine = downloadEngine;
    }

    public DownloadOptions downloadOptions;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(downloadId);
        dest.writeString(url);
        dest.writeString(md5);
        dest.writeString(path);
        dest.writeLong(fileLength);
        dest.writeInt(type);
        dest.writeLong(downloadSize);
        dest.writeInt(status);
    }


    public void readFromParcel(Parcel in) {
        downloadId = in.readString();
        url = in.readString();
        md5 = in.readString();
        path = in.readString();
        fileLength = in.readLong();
        type = in.readInt();
        downloadSize = in.readLong();
        status = in.readInt();
    }

    public static final Parcelable.Creator<Download> CREATOR = new Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Download download = (Download) o;
        return Objects.equals(downloadId, download.downloadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downloadId);
    }

    @Override
    public String toString() {
        return "Download{" +
                "id=" + id +
                ", downloadId='" + downloadId + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", path='" + path + '\'' +
                ", fileLength=" + fileLength +
                ", type=" + type +
                ", status=" + status +
                ", downloadSize=" + downloadSize +
                ", progress=" + progress +
//                ", downloadTaskList=" + downloadTaskList +
                ", downloadTaskRunableList=" + downloadTaskRunableList +
                '}';
    }
}
