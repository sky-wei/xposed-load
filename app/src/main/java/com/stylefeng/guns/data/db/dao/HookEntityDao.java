package com.stylefeng.guns.data.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.sky.xposed.load.data.db.convert.ListLongConverter;
import java.util.List;

import com.sky.xposed.load.data.db.entity.HookEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HOOK_ENTITY".
*/
public class HookEntityDao extends AbstractDao<HookEntity, Long> {

    public static final String TABLENAME = "HOOK_ENTITY";

    /**
     * Properties of entity HookEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PackageName = new Property(1, String.class, "packageName", false, "PACKAGE_NAME");
        public final static Property Ids = new Property(2, Long.class, "ids", false, "IDS");
    }

    private final ListLongConverter idsConverter = new ListLongConverter();

    public HookEntityDao(DaoConfig config) {
        super(config);
    }
    
    public HookEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HOOK_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PACKAGE_NAME\" TEXT," + // 1: packageName
                "\"IDS\" INTEGER);"); // 2: ids
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HOOK_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HookEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(2, packageName);
        }
 
        List ids = entity.getIds();
        if (ids != null) {
            stmt.bindLong(3, idsConverter.convertToDatabaseValue(ids));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HookEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(2, packageName);
        }
 
        List ids = entity.getIds();
        if (ids != null) {
            stmt.bindLong(3, idsConverter.convertToDatabaseValue(ids));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HookEntity readEntity(Cursor cursor, int offset) {
        HookEntity entity = new HookEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // packageName
            cursor.isNull(offset + 2) ? null : idsConverter.convertToEntityProperty(cursor.getLong(offset + 2)) // ids
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HookEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPackageName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIds(cursor.isNull(offset + 2) ? null : idsConverter.convertToEntityProperty(cursor.getLong(offset + 2)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HookEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HookEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HookEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
