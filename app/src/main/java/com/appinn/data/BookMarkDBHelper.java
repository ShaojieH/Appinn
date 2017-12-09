package com.appinn.data;
//bookmark database helper

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库类
 */
public class BookMarkDBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "appBookMarks.db";

    private static final int DATABASE_VERSION = 1;

    /**
     * 构造函数
     * @param context   context
     */
    public BookMarkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 初始化数据库
     * @param sqLiteDatabase    数据库
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE " + AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME + " (" +
                AppInfoContrast.AppInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE + " TEXT NOT NULL, " +
                AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT + " TEXT NOT NULL, " +
                //AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE + " TEXT NOT NULL, " +
                AppInfoContrast.AppInfoEntry.COLUMN_APP_URL + " TEXT NOT NULL, " +
                AppInfoContrast.AppInfoEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    /**
     * 更新时的操作
     * @param sqLiteDatabase    数据库
     * @param i 版本号?
     * @param i1    ??
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME);
        onCreate(sqLiteDatabase);
        */
    }
}
//TODO : move putToBookmark and removeFromBookmark to here