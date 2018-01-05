package com.sky.xposed.load.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sky.xposed.load.data.db.dao.DaoMaster;
import com.sky.xposed.load.data.db.dao.DaoSession;
import com.sky.xposed.load.data.db.dao.HookEntityDao;
import com.sky.xposed.load.data.db.dao.PluginEntityDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by sky on 18-1-4.
 */

public class DBManager {

    private static final String TAG = "DataManager";

    private static DBManager sDbManager;

    public static DBManager getInstance(Context context) {

        if (sDbManager == null) {
            synchronized (DBManager.class) {
                if (sDbManager == null) {
                    sDbManager = new DBManager(context.getApplicationContext());
                }
            }
        }
        return sDbManager;
    }

    private static final String DB_NAME = "load-db";

    private SQLiteDatabase mSqLiteDatabase;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public DBManager(Context context) {
        initialize(context);
    }

    private void initialize(Context context) {

        SkyOpenHelper helper = new SkyOpenHelper(context, DB_NAME);
        mSqLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mSqLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
    }

    public void release() {

        if (mDaoMaster == null) return ;

        mDaoSession.clear();
        mSqLiteDatabase.close();
        mDaoMaster = null;
    }

    public HookEntityDao getHookEntityDao() {
        return mDaoSession.getHookEntityDao();
    }

    public PluginEntityDao getPluginEntityDao() {
        return mDaoSession.getPluginEntityDao();
    }

    private final class SkyOpenHelper extends DaoMaster.OpenHelper {

        public SkyOpenHelper(Context context, String name) {
            super(context, name);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {

            // 数据库升级，需要处理的方法
            DaoMaster.dropAllTables(db, true);
            DaoMaster.createAllTables(db, false);
        }
    }
}
