package com.fangtang.tv.sdk.download.utils;

import android.util.Log;

public final class DL {

    private static String TAG = "Downloader";

    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static volatile boolean writeDebugLogs = true;
    private static volatile boolean writeLogs = true;

    private DL() {
    }

    public static void writeDebugLogs(boolean writeDebugLogs) {
        DL.writeDebugLogs = writeDebugLogs;
    }

    public static void writeLogs(boolean writeLogs) {
        DL.writeLogs = writeLogs;
    }

    public static void d(String message, Object... args) {
        if (writeDebugLogs) {
            log(Log.DEBUG, null, message, args);
        }
    }

    public static void i(String message, Object... args) {
        log(Log.INFO, null, message, args);
    }

    public static void w(String message, Object... args) {
        log(Log.WARN, null, message, args);
    }

    public static void e(Throwable ex) {
        log(Log.ERROR, ex, null);
    }

    public static void e(String message, Object... args) {
        log(Log.ERROR, null, message, args);
    }

    public static void e(Throwable ex, String message, Object... args) {
        log(Log.ERROR, ex, message, args);
    }

    private static void log(int priority, Throwable ex, String message, Object... args) {
        if (!writeLogs) return;
        if (args.length > 0) {
            message = String.format(message, args);
        }

        String log;
        if (ex == null) {
            log = message;
        } else {
            String logMessage = message == null ? ex.getMessage() : message;
            String logBody = Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }
        Log.println(priority, TAG, log);
    }
}