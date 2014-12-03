package com.cnx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * SQLiteDatabase tools
 * @author david.xu
 *
 */
public class DBHelper extends SQLiteOpenHelper {
    /**
     * 
     * @param context 
     * @param name  database name
     * @param factory 
     * @param version 
     */
    public DBHelper(Context context, String name, CursorFactory factory,int version) {
        super(context, name, factory, version);
    }
    /**
     * create DBHelper instance
     * @param context 
     */
    public DBHelper(Context context){
        this(context, DBInfo.DB.DB_NAME, null, DBInfo.DB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBInfo.Table.CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBInfo.Table.DROP_USER_TABLE);
        onCreate(db);
    }

}
