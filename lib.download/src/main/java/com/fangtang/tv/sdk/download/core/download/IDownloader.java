package com.fangtang.tv.sdk.download.core.download;

import java.io.IOException;
import java.io.InputStream;

public interface IDownloader {

    InputStream getStream(String videoUri, Object extra) throws IOException;

    InputStream getStream(String videoUri, long rangeStart, long rangeEnd, Object extra) throws IOException;
}
