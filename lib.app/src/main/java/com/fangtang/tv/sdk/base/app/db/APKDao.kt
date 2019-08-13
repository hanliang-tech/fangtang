package com.fangtang.tv.sdk.base.app.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.fangtang.tv.sdk.base.app.AppInfoBean
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_APP_ID_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_ICON_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_MD5_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_NAME_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_PCK_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_URL_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.COLUMN_VERSION_VALUE
import com.fangtang.tv.sdk.base.app.db.APKDBHelper.Companion.TABLE_NAME


class APKDao(context: Context) {

    private var appDBHelper: APKDBHelper = APKDBHelper(context)


    fun insert(dataBean: AppInfoBean) {
        try {
            val values = ContentValues().apply {
                put(COLUMN_APP_ID_VALUE, dataBean.appId)
                put(COLUMN_NAME_VALUE, dataBean.name)
                put(COLUMN_PCK_VALUE, dataBean.packageName)
                put(COLUMN_URL_VALUE, dataBean.url)
                put(COLUMN_MD5_VALUE, dataBean.md5)
                put(COLUMN_VERSION_VALUE, dataBean.versionCode)
                put(COLUMN_ICON_VALUE, dataBean.icon)
            }

            appDBHelper.writableDatabase.replace(TABLE_NAME, null, values)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun insert(appId: String?, name: String, package_name: String, url: String, md5: String, version: String, icon: String) {
        val values = ContentValues().apply {
            put(COLUMN_APP_ID_VALUE, appId)
            put(COLUMN_NAME_VALUE, name)
            put(COLUMN_PCK_VALUE, package_name)
            put(COLUMN_URL_VALUE, url)
            put(COLUMN_MD5_VALUE, md5)
            put(COLUMN_VERSION_VALUE, version)
            put(COLUMN_ICON_VALUE, icon)
        }

        appDBHelper.writableDatabase.replace(TABLE_NAME, null, values)
    }

    fun query(): MutableList<AppInfoBean>? {
        var cursor: Cursor? = null
        try {
            val db = appDBHelper.readableDatabase
            cursor = db.query(
                    TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            )
            val item = mutableListOf<AppInfoBean>()
            with(cursor) {
                while (moveToNext()) {
                    val listBean = AppInfoBean()
                    listBean.appId = getString(getColumnIndexOrThrow(COLUMN_APP_ID_VALUE))
                    listBean.name = getString(getColumnIndexOrThrow(COLUMN_NAME_VALUE))
                    listBean.packageName = getString(getColumnIndexOrThrow(COLUMN_PCK_VALUE))
                    listBean.url = getString(getColumnIndexOrThrow(COLUMN_URL_VALUE))
                    listBean.md5 = getString(getColumnIndexOrThrow(COLUMN_MD5_VALUE))
                    listBean.versionCode = getString(getColumnIndexOrThrow(COLUMN_VERSION_VALUE))
                    listBean.icon = getString(getColumnIndexOrThrow(COLUMN_ICON_VALUE))
                    item.add(listBean)
                }
            }
            return item
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    fun query(key: String, value: String): MutableList<AppInfoBean>? {
        var cursor: Cursor? = null
        try {
            val db = appDBHelper.readableDatabase
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $key = ?", arrayOf(value))
            val item = mutableListOf<AppInfoBean>()
            with(cursor) {
                while (moveToNext()) {
                    val listBean = AppInfoBean()
                    listBean.appId = getString(getColumnIndexOrThrow(COLUMN_APP_ID_VALUE))
                    listBean.name = getString(getColumnIndexOrThrow(COLUMN_NAME_VALUE))
                    listBean.packageName = getString(getColumnIndexOrThrow(COLUMN_PCK_VALUE))
                    listBean.url = getString(getColumnIndexOrThrow(COLUMN_URL_VALUE))
                    listBean.md5 = getString(getColumnIndexOrThrow(COLUMN_MD5_VALUE))
                    listBean.versionCode = getString(getColumnIndexOrThrow(COLUMN_VERSION_VALUE))
                    listBean.icon = getString(getColumnIndexOrThrow(COLUMN_ICON_VALUE))
                    item.add(listBean)
                }
            }
            return item
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
}