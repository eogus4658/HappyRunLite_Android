package com.example.happyrunlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFLIE_HAPPYRUN = "happyrun.db";

    private  static DBManager m_dbManager = null;





    public DBManager(Context context) {
        super(context, DBFLIE_HAPPYRUN, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + "HAPPYRUNDB" + " " +
                "(" +
                "RECORD" +        " INTEGER NOT NULL" +   ", " +
                "DATE" +      " TEXT"             +
                ")" ;
        db.execSQL(SQL_CREATE_TBL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // onUpgrade(db, oldVersion, newVersion)
    }

    public static DBManager getInstance(Context context){
        if(m_dbManager == null){
            m_dbManager = new DBManager(context);
        }
        return m_dbManager;
    }

}