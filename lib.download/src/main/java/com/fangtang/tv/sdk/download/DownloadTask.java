package com.fangtang.tv.sdk.download;

public class DownloadTask {

    private Long id;
    private String downloadId;
    private String downloadTaskId;
    private String url;
    private long range;
    private long downloadSize;
    private long fileLength;
    private int status = -2;

    public DownloadTask() {
    }

    public DownloadTask(Long id) {
        this.id = id;
    }

    public DownloadTask(Long id, String downloadId, String downloadTaskId, String url, Long range, Long downloadSize, Long fileLength, Integer status) {
        this.id = id;
        this.downloadId = downloadId;
        this.downloadTaskId = downloadTaskId;
        this.url = url;
        this.range = range;
        this.downloadSize = downloadSize;
        this.fileLength = fileLength;
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

    public String getDownloadTaskId() {
        return downloadTaskId;
    }

    public void setDownloadTaskId(String downloadTaskId) {
        this.downloadTaskId = downloadTaskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getRange() {
        return range;
    }

    public void setRange(long range) {
        this.range = range;
    }

    public Long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "DownloadTask{" +
                "id=" + id +
                ", downloadId='" + downloadId + '\'' +
                ", downloadTaskId='" + downloadTaskId + '\'' +
                ", url='" + url + '\'' +
                ", range=" + range +
                ", downloadSize=" + downloadSize +
                ", fileLength=" + fileLength +
                ", status=" + status +
                '}';
    }
}
