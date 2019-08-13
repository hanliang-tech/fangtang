package com.fangtang.tv.sdk.download.core.download;

import android.content.Context;
import android.net.Uri;

import com.fangtang.tv.sdk.download.core.assist.ContentLengthAndTypeInputStream;
import com.fangtang.tv.sdk.download.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 下载器
 * Created by liulipeng on 15-9-6.
 */
public class BaseDownloader implements IDownloader {

    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    protected static final int MAX_REDIRECT_COUNT = 5;

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseDownloader(Context context) {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public BaseDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(String videoUri, Object extra) throws IOException {
        return getStreamFromNetwork(videoUri, -1, -1, extra);
    }

    @Override
    public InputStream getStream(String videoUri, long rangeStart, long rangeEnd, Object extra) throws IOException {
        return getStreamFromNetwork(videoUri, rangeStart, rangeEnd, extra);
    }

    protected InputStream getStreamFromNetwork(String videoUri, long rangeStart, long rangeEnd, Object extra) throws IOException {

        HttpURLConnection conn = createConnection(videoUri, rangeStart, rangeEnd, extra);

        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"), rangeStart, rangeEnd, extra);
            redirectCount++;
        }

        InputStream videoStream;
        try {
            videoStream = conn.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        if (!shouldBeProcessed(conn)) {
            IoUtils.closeSilently(videoStream);
            throw new IOException("download request failed with response code " + conn.getResponseCode());
        }
//        DL.e(conn.getContentLength() + "#<<<<<<--长度--视频的----类型------->>>>>>" + conn.getContentType());

        Map<String, String> headerMap = getHttpResponseHeader(conn);
        for(Map.Entry<String, String> entry : headerMap.entrySet()){
            String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";
//            DL.e(key + "#------header----------"+ entry.getValue());
        }
        return new ContentLengthAndTypeInputStream(new BufferedInputStream(videoStream, BUFFER_SIZE),
                conn.getContentLength(), conn.getContentType(), conn.getHeaderField("Accept-Ranges"));
    }

    protected boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
//        DL.e("#--------ResponseCode------->>>>" + conn.getResponseCode());
        return conn.getResponseCode() < 300;
    }

    public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0;; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null) break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }

    protected HttpURLConnection createConnection(String url, long rangeStart, long rangeEnd, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        if (rangeStart != -1 && rangeEnd != -1) {
            conn.setRequestProperty("Range", "bytes=" + rangeStart + "-" + rangeEnd);//设置获取实体数据的范围
        }
        conn.setReadTimeout(readTimeout);
        return conn;
    }
}
