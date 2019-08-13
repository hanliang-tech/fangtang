package com.fangtang.tv.sdk.base.scanner

import android.content.Context
import android.text.format.DateUtils
import com.fangtang.tv.sdk.base.device.DeviceManager
import com.fangtang.tv.sdk.base.kv.IKVManager
import com.fangtang.tv.sdk.base.logging.logD
import com.fangtang.tv.sdk.base.net.KDefaultCallBack
import com.fangtang.tv.sdk.base.remote.IRemoteManager
import com.fangtang.tv.sdk.base.remote.bean.RemoteDeviceBindInfo
import com.fangtang.tv.sdk.base.scanner.server.ScanStrategyEntity
import com.fangtang.tv.sdk.base.scanner.server.StrategyAPIImpl
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import java.util.*

class ScannerManager : IScannerManager {

    companion object {
        private const val TAG = "ScannerManager"
    }

    private val deviceScannerListenerList: MutableList<IScannerManager.DeviceScannerListener> =
            Collections.synchronizedList(mutableListOf())

    private val supportList = SupportDevice::class.java.fields
            .map {
                it.get(null) as SupportDevice
            }

    private val scope by lazy {
        MainScope()
    }

    private val threadPool = newFixedThreadPoolContext(getThreadCount(), "scanner_bg")

    private lateinit var context: Context
    private lateinit var deviceManager: DeviceManager
    private lateinit var kvManager: IKVManager
    private lateinit var remoteManager: IRemoteManager

    @Synchronized
    fun init(configuration: ScanConfiguration, kvManager: IKVManager, deviceManager: DeviceManager, remoteManager: IRemoteManager) {
        this.context = configuration.context
        this.kvManager = kvManager
        this.deviceManager = deviceManager
        this.remoteManager = remoteManager
    }

    /**
     * 1、检测绑定的设备数
     * 2、从网络获取扫描规则
     */
    override fun startScan() {
        logD { "调用扫描" }
        checkBindInfo()
    }

    private fun checkBindInfo(){
        remoteManager.getBindDeviceInfo(object :IRemoteManager.RemoteDeviceBindInfoListener{
            override fun onBindInfoSuccess(bindInfo: RemoteDeviceBindInfo) {
                if(bindInfo.isBindIotDevice){
                    logD { "已经绑定 不扫描" }
                    return
                }
                logD { "没有绑定设备" }
                checkStrategy()
            }

            override fun onBindInfoError(e: java.lang.Exception?) {
                logD { "获取绑定信息出错" }
                checkStrategy()
            }

        })
    }

    private fun checkStrategy() {
        StrategyAPIImpl(deviceManager).reqScanStrategy(KDefaultCallBack { code, entity ->
            entity?.data?.let { strategy ->
                logD { "获取扫描规则" }
                applyStrategy(strategy)
            }
        })
    }

    private fun applyStrategy(strategy: ScanStrategyEntity.StrategyBean) {
        try {
            val kv = kvManager
            val calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.UK)
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.minimalDaysInFirstWeek = 7
//            val calendar = Calendar.getInstance()
//            calendar.firstDayOfWeek = Calendar.MONDAY

            val sMonth = ScanStrategy("scan_month", Calendar.MONTH, kv, calendar, strategy.monthNum.toInt())
            val sWeek = ScanStrategy("scan_week", Calendar.WEEK_OF_YEAR, kv, calendar, strategy.weekNum.toInt())
            val sDay = ScanStrategy("scan_day", Calendar.DAY_OF_YEAR, kv, calendar, strategy.dayNum.toInt())

            if (sMonth.pass() && sWeek.pass() && sDay.pass()) {
                searchDevice(strategy.maxBlockDeviceNum.toInt()) {
                    notifyDeviceScanned(it)
                    sDay.update()
                    sWeek.update()
                    sMonth.update()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun searchDevice(limitSize:Int, callBack: (ScanDevice) -> Unit) {
        scope.launch {
            async(threadPool) {
                delay(DateUtils.SECOND_IN_MILLIS * 10)
                logD { "开始寻找..." }
                // 1. 获取本机IP
                val hostIpSuffix = NetworkInterface.getNetworkInterfaces().let ip@{
                    while (it.hasMoreElements()) {
                        it.nextElement().let {
                            it.inetAddresses.let {
                                while (it.hasMoreElements()) {
                                    it.nextElement().let {
                                        val hostIp = it.hostAddress
                                        if (it !is Inet6Address && "127.0.0.1" != hostIp) {
                                            return@ip hostIp.substring(0, hostIp.lastIndexOf(".") + 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                logD { "Get ip: $hostIpSuffix" }
                // 2. 获取从路由器同步的ARP
                val macList = HashSet<String>()
                updateArpList(macList)

                // 3. 向所有设备发送UDP 30次
                val count = 30
                for (i in 0 until count) {
                    sendPck(hostIpSuffix)
                    updateArpList(macList)
                    if (i != count - 1) {
                        delay(500L)
                    }
                }

                logD { "发现的mac数量:${macList.size}" }
                // 读取设备表
//                val companyList = HashSet<String>()
//                Utils.getApp().assets.open("mac.txt").bufferedReader().use {
//                    it.readLines().associate {
//                        val sp = it.split("@")
//                        Pair(sp[0], sp[1])
//                    }.let { companyDataMap ->
////                        if (FLog.isLoggable(FLog.VERBOSE)) {
////                            FLog.v(TAG, "公司数量:${companyDataMap.size}")
////                        }
//                        macList.forEach br@{ mac ->
//                            val fixMac = mac.substring(0, 8).replace(":", "").toUpperCase()
//                            if (companyDataMap.containsKey(fixMac)) {
//                                logD { "寻找到:$fixMac" }
//                                companyList.add(companyDataMap[fixMac]!!)
//                                return@br
//                            }
//                        }
//                    }
//                }
//
//                if (FLog.isLoggable(FLog.VERBOSE)) {
//                    FLog.v(TAG, "过滤支持列表+++")
//                }
//                // 过滤匹配到的设备并返回
//                supportList.filter { companyList.contains(it.company) }

                val fixMacList = macList.map { it.substring(0, 8).replace(":", "").toUpperCase() }
                supportList.filter { fixMacList.contains(it.macPrefix) }

            }.await().let {
                logD { "过滤后的设备数:${it.size}" }
                if (it.isNotEmpty() && it.size <= limitSize) {
                    val device = ScanDevice()
                    device.deviceName = it.first().displayName
                    callBack(device)
                }
            }
        }
    }

    override fun stopScan() {
        scope.cancel()
    }

    override fun addDeviceScannerListener(listener: IScannerManager.DeviceScannerListener): Boolean {
        if (!deviceScannerListenerList.contains(listener)) {
            return deviceScannerListenerList.add(listener)
        }
        return false
    }

    override fun removeDeviceScannerListener(listener: IScannerManager.DeviceScannerListener): Boolean {
        return deviceScannerListenerList.remove(listener)
    }

    private fun notifyDeviceScanned(device: ScanDevice) {
        for (listener in deviceScannerListenerList) {
            try {
                listener.onScanDevice(device)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun release() {
        deviceScannerListenerList.clear()
    }

    private fun updateArpList(list: HashSet<String>) {
        val process = Runtime.getRuntime().exec("cat proc/net/arp")
        BufferedReader(InputStreamReader(process.inputStream)).use {
            var line: String?
            do {
                line = it.readLine()
                if (line != null && !line.contains("00:00:00:00:00:00") && !line.contains("IP")) {
                    val split = line.split("\\s+".toRegex())
                    list.add(split[3])
                }
            } while (line != null)
        }
    }

    private fun sendPck(hostIpSuffix: Any) {
        val sendPck = DatagramPacket(ByteArray(0), 0, 0)
        var socket = DatagramSocket()

        try {
            var ip = 2
            while (ip < 255) {
                sendPck.address = InetAddress.getByName("$hostIpSuffix$ip")
                //                sendPck.port = 137
                socket.send(sendPck)
                ip++
                if (ip == 125) {
                    socket.close()
                    socket = DatagramSocket()
                }
            }
        } catch (e: Throwable) {
        } finally {
            if (!socket.isClosed) {
                socket.close()
            }
        }
    }

    private fun getThreadCount(): Int {
        return 1
    }
}