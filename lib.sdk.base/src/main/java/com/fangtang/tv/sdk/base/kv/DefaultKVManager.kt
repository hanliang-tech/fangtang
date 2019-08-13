package com.fangtang.tv.sdk.base.kv

import android.content.Context

class DefaultKVManager(context: Context): IKVManager {

    val sp = context.getSharedPreferences("sdk_sp", Context.MODE_PRIVATE)
    val edit = sp.edit()

    override fun putString(key: String?, value: String?) {
        edit.putString(key, value).apply()
    }

    override fun putInt(key: String?, value: Int) {
        edit.putInt(key, value).apply()
    }

    override fun putFloat(key: String?, value: Float) {
        edit.putFloat(key, value).apply()
    }

    override fun putLong(key: String?, value: Long) {
        edit.putLong(key, value).apply()
    }

    override fun putBoolean(key: String?, value: Boolean) {
        edit.putBoolean(key, value).apply()
    }

    override fun getString(key: String?, defaultValue: String?): String? {
        return sp.getString(key, defaultValue)
    }

    override fun getInt(key: String?, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    override fun getFloat(key: String?, defaultValue: Float): Float {
        return sp.getFloat(key, defaultValue)
    }

    override fun getLong(key: String?, defaultValue: Long): Long {
        return sp.getLong(key, defaultValue)
    }

    override fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

}