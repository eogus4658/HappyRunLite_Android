package com.example.happyrunlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBManager extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFLIE_HAPPYRUN = "happyrun.db";

    private  static DBManager m_dbManager = null;
    // ------------------------------------------------------------------
    public static DBManager getInstance(Context context){
        if(m_dbManager == null){
            m_dbManager = new DBManager(context);
        }
        return m_dbManager;
    }


    public DBManager(Context context) {
        super(context, DBFLIE_HAPPYRUN, null, DB_VERSION);
    }

    // ------------------------------------------------------------------
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

    // ------------------------------------------------------------------
    public  void save_values(String strtime, String strdist ) {
        SQLiteDatabase db = getWritableDatabase();

        String strrecord = strtime + " , " + strdist;

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다.
        String nowTime = sdfNow.format(date);

        String sqlInsert = "INSERT INTO HAPPYRUNDB " +
                "(RECORD , DATE) VALUES (" +
                "'" + strrecord + "'," +
                "'" + nowTime + "'" + ")" ;

        System.out.println(sqlInsert) ;

        db.execSQL(sqlInsert) ;
    }

    // ------------------------------------------------------------------
    public Cursor load_db_cursor() {
        SQLiteDatabase db = getReadableDatabase() ;
        String SQL_SELECT = "SELECT * FROM " + "HAPPYRUNDB" ;
        Cursor cursor = db.rawQuery(SQL_SELECT, null) ;
        return cursor;
    }

    // ------------------------------------------------------------------
    public void delete_values(String date){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM HAPPYRUNDB WHERE DATE = '" + date + "'");
    }

}