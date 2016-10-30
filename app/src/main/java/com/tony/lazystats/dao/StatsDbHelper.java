package com.tony.lazystats.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tony on 29/10/16.
 */

public class StatsDbHelper extends SQLiteOpenHelper {
    // Increment db version by one if you change the database schema
    public static final int DATABASE_VERSION = 1;

    public static StatsDbHelper instance;

    private StatsDbHelper(Context context){
        super(context,LazyStatsContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static StatsDbHelper getInstance(Context context){
        if(instance == null){
            instance = new StatsDbHelper(context.getApplicationContext());
        }
        return instance;
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
