package com.alice377.alice377_android.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.alice377.alice377_android.Alice377_android.appname;
import static com.alice377.alice377_android.Alice377_android.uselog;

public class AppLogDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "alice377.db"; //資料庫名稱
    private static final int VERSION = 1; //資料庫版本，資料庫結構改變時要更改數字，通常+1
    private static final String DB_TABLE = "alice377_user_action"; //資料表名稱
    private static final String crTBsql = "CREATE TABLE " + DB_TABLE + "("
            + "id INTEGER PRIMARY KEY," + "action_view TEXT NOT NULL," + "app_name TEXT NOT NULL,"
            + "action_action TEXT NOT NULL," + "action_date TEXT NOT NULL," + "status TEXT NOT NULL,"
            + "insert_date TEXT NOT NULL" + ")";
    public static String action_view = ""; //記錄使用者所在view
    public static String action_action = ""; //記錄使用者動作
    public static String action_date = ""; //記錄使用者執行動作的時間
    public static int status = 0; //記錄執行結果
    public static String insert_date = ""; //記錄資料寫入的時間
    static String TAG = "alice377=>";
    private static SQLiteDatabase db_alice377_list; //資料庫名稱


    private AppLogDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {

        if (db_alice377_list == null || !db_alice377_list.isOpen())
            db_alice377_list = new AppLogDb(context, DB_NAME, null, VERSION).getWritableDatabase();

        return db_alice377_list;
    }

    //直接傳入SQLite語法做查詢
    public static Cursor rawquery(Context context, String sql, String[] selectionArgs) {
        getDatabase(context); //開啟資料庫
        Cursor c = null;

        try {
            c = db_alice377_list.rawQuery(sql, selectionArgs);

        } catch (Exception e) {
            if (uselog) Log.d(TAG, "error=" + e.toString());
        }

        return c;
    }

    //物件導向式SQLite查詢
    public static Cursor query() {
        Cursor c = null;
        //code構思中...

        return c;
    }

    //新增資料：寫log
    public static long insert(Context context) {
        getDatabase(context); //開啟資料庫
        ContentValues newRow = new ContentValues();
        newRow.put("app_name", appname);
        newRow.put("action_view", action_view);
        newRow.put("action_action", action_action);
        newRow.put("action_date", action_date);
        newRow.put("status", status);
        newRow.put("insert_date", insert_date);
        long rowID = db_alice377_list.insert(DB_TABLE, null, newRow);
        db_alice377_list.close(); //寫入完成關閉

        return rowID;
    }

    //刪除資料：殺log
    public static int delete(Context context, String where) {
        getDatabase(context); //開啟資料庫
        String sql = "select * from " + DB_TABLE;
        int rowsAffected = -1;

        try {
            Cursor c = db_alice377_list.rawQuery(sql, null);

            if (c != null) {

                if (c.getCount() != 0) {
                    rowsAffected = db_alice377_list.delete(DB_TABLE, where, null);
                    db_alice377_list.close();
                }

                c.close();
            }

        } catch (Exception e) {
            if (uselog) Log.d(TAG, "error=" + e.toString());

        } finally {
            if (db_alice377_list != null) db_alice377_list.close();
        }

        return rowsAffected;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crTBsql);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    //資料庫升版：系統自動偵測調用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE);
        onCreate(db);
    }
}
