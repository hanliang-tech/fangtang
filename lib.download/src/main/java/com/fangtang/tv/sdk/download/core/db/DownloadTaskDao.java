package com.fangtang.tv.sdk.download.core.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.fangtang.tv.sdk.download.DownloadTask;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


public class DownloadTaskDao extends AbstractDao<DownloadTask, Long> {

    public static final String TABLENAME = "downloadtask";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DownloadId = new Property(1, String.class, "downloadId", false, "DOWNLOAD_ID");
        public final static Property DownloadTaskId = new Property(2, String.class, "downloadTaskId", false, "DOWNLOAD_TASK_ID");
        public final static Property Url = new Property(3, String.class, "url", false, "URL");
        public final static Property Range = new Property(4, Long.class, "range", false, "RANGE");
        public final static Property DownloadSize = new Property(5, Long.class, "downloadSize", false, "DOWNLOAD_SIZE");
        public final static Property FileLength = new Property(6, Long.class, "fileLength", false, "FILE_LENGTH");
        public final static Property Status = new Property(7, Integer.class, "status", false, "STATUS");
    }

    public DownloadTaskDao(DaoConfig config) {
        super(config);
    }

    public DownloadTaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"downloadtask\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DOWNLOAD_ID\" TEXT NOT NULL ," + // 1: vid
                "\"DOWNLOAD_TASK_ID\" TEXT NOT NULL ," + // 1: vid
                "\"URL\" TEXT NOT NULL ," + // 3: tsUrl
                "\"RANGE\" INTEGER," + // 4: range
                "\"DOWNLOAD_SIZE\" INTEGER," + // 5: downloadSize
                "\"FILE_LENGTH\" INTEGER," + // 6: fileLength
                "\"STATUS\" INTEGER);"); // 7: status
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"downloadtask\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, DownloadTask entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        stmt.bindString(2, entity.getDownloadId());
        stmt.bindString(3, entity.getDownloadTaskId());
        stmt.bindString(4, entity.getUrl());

        Long range = entity.getRange();
        if (range != null) {
            stmt.bindLong(5, range);
        }

        Long downloadSize = entity.getDownloadSize();
        if (downloadSize != null) {
            stmt.bindLong(6, downloadSize);
        }

        Long fileLength = entity.getFileLength();
        if (fileLength != null) {
            stmt.bindLong(7, fileLength);
        }

        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(8, status);
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
    public DownloadTask readEntity(Cursor cursor, int offset) {
        DownloadTask entity = new DownloadTask( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // downloadId
                cursor.getString(offset + 2), // d
                cursor.getString(offset + 3), // url
                cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // range
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // downloadSize
                cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // fileLength
                cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7)//status
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, DownloadTask entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDownloadId(cursor.getString(offset + 1));
        entity.setDownloadTaskId(cursor.getString(offset + 2));
        entity.setUrl(cursor.getString(offset + 3));
        entity.setRange(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setDownloadSize(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setFileLength(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setStatus(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(DownloadTask entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(DownloadTask entity) {
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
