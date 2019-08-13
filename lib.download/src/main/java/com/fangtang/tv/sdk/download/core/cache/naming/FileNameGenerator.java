package com.fangtang.tv.sdk.download.core.cache.naming;

import com.fangtang.tv.sdk.download.Download;

public interface FileNameGenerator {

    String generate(Download download);
}
