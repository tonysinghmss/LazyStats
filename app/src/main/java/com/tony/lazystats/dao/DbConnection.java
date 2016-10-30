package com.tony.lazystats.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tony on 30/10/16.
 */

public class DbConnection {
    private SQLiteOpenHelper dbHelper;

    public DbConnection(Context context){
        dbHelper = StatsDbHelper.getInstance(context);
    }

    public SQLiteDatabase openr(){
        return dbHelper.getReadableDatabase();
    }

    public SQLiteDatabase openw(){
        return dbHelper.getWritableDatabase();
    }
}
