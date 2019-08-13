package com.fangtang.tv.sdk.download.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.fangtang.tv.sdk.download.Download;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


/**
 * DAO for table "download".
 */
public class DownloadDao extends AbstractDao<Download, Long> {

    public static final String TABLENAME = "download";

    /**
     * Properties of entity Download.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DownloadId = new Property(1, String.class, "downloadId", false, "DOWNLOAD_ID");
        public final static Property Url = new Property(2, String.class, "url", false, "URL");
        public final static Property Md5 = new Property(3, String.class, "md5", false, "MD5");
        public final static Property Path = new Property(4, String.class, "path", false, "PATH");
        public final static Property FileLength = new Property(5, Long.class, "fileLength", false, "FILE_LENGTH");
        public final static Property Type = new Property(6, Integer.class, "type", false, "TYPE");
        public final static Property DownloadSize = new Property(7, Long.class, "downloadSize", false, "DOWNLOAD_SIZE");
        public final static Property Status = new Property(8, Integer.class, "status", false, "STATUS");
    }


    public DownloadDao(DaoConfig config) {
        super(config);
    }

    public DownloadDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"download\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DOWNLOAD_ID\" TEXT NOT NULL ," + // 1: vid
                "\"URL\" TEXT NOT NULL ," + // 2: url
                "\"MD5\" TEXT," + // 3: name
                "\"PATH\" TEXT," + // 4: path
                "\"FILE_LENGTH\" INTEGER," + // 5: fileLength
                "\"TYPE\" INTEGER," + // 6: type
                "\"DOWNLOAD_SIZE\" INTEGER," + // 7: downloadSize
                "\"STATUS\" INTEGER);"); // 8: status
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"download\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, Download entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getDownloadId());
        stmt.bindString(3, entity.getUrl());

        String md5 = entity.getMd5();
        if (md5 != null) {
            stmt.bindString(4, md5);
        }

        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(5, path);
        }

        Long fileLength = entity.getFileLength();
        if (fileLength != null) {
            stmt.bindLong(6, fileLength);
        }

        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(7, type);
        }

        Long downloadSize = entity.getDownloadSize();
        if (downloadSize != null) {
            stmt.bindLong(8, downloadSize);
        }

        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(9, status);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Download readEntity(Cursor cursor, int offset) {
        Download entity = new Download( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // downloadId
                cursor.getString(offset + 2),//url
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // md5
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // path
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // filelength
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // type
                cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // downloadSize
                cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8) // status
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, Download entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDownloadId(cursor.getString(offset + 1));
        entity.setUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMd5(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFileLength(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setDownloadSize(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setStatus(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(Download entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(Download entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
