package com.fangtang.tv.sdk.download.core.cache.naming;

import com.fangtang.tv.sdk.download.Download;

public class ExtensionFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(Download download) {
        return String.valueOf(download.getDownloadId());
    }
}
