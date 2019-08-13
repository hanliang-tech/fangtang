package com.fangtang.tv.sdk.base.app.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class APKDBHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "app.db"

        const val TABLE_NAME = "apk"
        const val COLUMN_NAME_VALUE = "name"
        const val COLUMN_APP_ID_VALUE = "app_id"
        const val COLUMN_PCK_VALUE = "package_name"
        const val COLUMN_URL_VALUE = "url"
        const val COLUMN_MD5_VALUE = "md5"
        const val COLUMN_VERSION_VALUE = "version"
        const val COLUMN_ICON_VALUE = "icon"

        const val SQL_CREATE_ENTRIES =
                "CREATE TABLE $TABLE_NAME (" +
                        "${BaseColumns._ID} INTEGER," +
                        "$COLUMN_NAME_VALUE TEXT," +
                        "$COLUMN_APP_ID_VALUE TEXT," +
                        "$COLUMN_PCK_VALUE TEXT PRIMARY KEY," +
                        "$COLUMN_URL_VALUE TEXT," +
                        "$COLUMN_MD5_VALUE TEXT," +
                        "$COLUMN_VERSION_VALUE TEXT," +
                        "$COLUMN_ICON_VALUE TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}