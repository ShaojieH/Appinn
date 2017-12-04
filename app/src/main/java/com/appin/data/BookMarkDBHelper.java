package com.appin.data;
//bookmark database helper

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookMarkDBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "appBookMarks.db";

    private static final int DATABASE_VERSION = 1;

    public BookMarkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE " + AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME + " (" +
                AppInfoContrast.AppInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AppInfoContrast.AppInfoEntry.COLUMN_APP_TITLE + " TEXT NOT NULL, " +
                //AppInfoContrast.AppInfoEntry.COLUMN_APP_ABSTRACT + " TEXT NOT NULL, " +
                //AppInfoContrast.AppInfoEntry.COLUMN_APP_TYPE + " TEXT NOT NULL, " +
                AppInfoContrast.AppInfoEntry.COLUMN_APP_URL + " TEXT NOT NULL, " +
                AppInfoContrast.AppInfoEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppInfoContrast.AppInfoEntry.BOOK_MARK_TABLE_NAME);
        onCreate(sqLiteDatabase);
        */
    }
}
//TODO : move putToBookmark and removeFromBookmark to here