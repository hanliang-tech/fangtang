package com.fangtang.tv.sdk.base.scanner;

public interface IScannerManager {

    void startScan();

    void stopScan();

    interface DeviceScannerListener {
        void onScanDevice(ScanDevice device);
    }

    boolean addDeviceScannerListener(DeviceScannerListener listener);

    boolean removeDeviceScannerListener(DeviceScannerListener listener);

    void release();
}
