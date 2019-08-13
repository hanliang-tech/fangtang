package com.fangtang.tv.sdk.download.core.cache.naming;


import com.fangtang.tv.sdk.download.Download;

public class NoOpFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(Download download) {
        return download.getDownloadId() + ".apk";
    }
}
