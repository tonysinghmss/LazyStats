package com.tony.lazystats.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tony on 29/10/16.
 */

public class StatCreationDbHelper extends SQLiteOpenHelper {
    // Increment db version by one if you change the database schema
    public static final int DATABASE_VERSION = 1;

    public StatCreationDbHelper(Context context){
        super(context,LazyStatsContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table statistics in database
        db.execSQL(LazyStatsContract.SQL_CREATE_STATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
