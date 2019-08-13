package com.fangtang.tv.sdk.download.view.model;


import java.util.Objects;

public class DownloadViewModel {

    public String id;
    public String name;
    public String path;
    public String state;
    public float progress;
    public String query;
    public String pck;
    public String appId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadViewModel that = (DownloadViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DownloadViewModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", state='" + state + '\'' +
                ", progress='" + progress + '\'' +
                ", query='" + query + '\'' +
                ", pck='" + pck + '\'' +
                ", appId='" + appId + '\'' +
                '}';
    }
}
