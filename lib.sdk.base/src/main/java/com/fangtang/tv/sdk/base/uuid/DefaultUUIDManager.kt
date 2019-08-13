package com.fangtang.tv.sdk.base.uuid

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.fangtang.tv.sdk.base.kv.IKVManager
import com.fangtang.tv.sdk.base.util.MacIDHelper
import com.fangtang.tv.sdk.base.util.MarketUtil
import java.util.*


class DefaultUUIDManager(private val context: Context, private val kvManager: IKVManager) : IUUIDManager {

    companion object {
        const val UUID_KEY = "uuid"
        var DEVICE_UUID = ""
    }

    override fun generateUUID(): String {
        if (!TextUtils.isEmpty(DEVICE_UUID)) {
            return DEVICE_UUID
        }
        var uuid = kvManager.getString(UUID_KEY, "")
        if (!TextUtils.isEmpty(uuid)) {
            DEVICE_UUID = uuid
            return uuid
        }

        uuid = getUniquePsuedoID()
        kvManager.putString(UUID_KEY, uuid)
        DEVICE_UUID = uuid
        return uuid
    }

    private fun getUniquePsuedoID(): String {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        val pck = MarketUtil.getPackageName()
        val m_szDevIDShort = "35" +
                pck +
                Build.BOARD.length % 10 +
                Build.DISPLAY.length % 10 +
                Build.CPU_ABI.length % 10 +
                Build.DEVICE.length % 10 +
                Build.MANUFACTURER.length % 10 +
                Build.MODEL.length % 10 +
                Build.ID.length % 10 +
                Build.TAGS.length % 10 +
                Build.TYPE.length % 10 +
                Build.PRODUCT.length % 10 +
                Build.USER.length % 10

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        val mac = MacIDHelper.getMacAddress(context.applicationContext)
        var serial: String? = null
        try {
            serial = Build::class.java.getField("SERIAL").get(null).toString()
            val seed = serial + m_szDevIDShort + mac
//            return UUID.fromString(seed).toString()
            // Go ahead and return the serial for api => 9
            return UUID(m_szDevIDShort.hashCode().toLong(), seed.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            // String needs to be initialized
            exception.printStackTrace()
            serial = mac + m_szDevIDShort // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return UUID(m_szDevIDShort.hashCode().toLong(), serial!!.hashCode().toLong()).toString()
    }
}