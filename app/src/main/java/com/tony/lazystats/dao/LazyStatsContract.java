package com.tony.lazystats.dao;

import android.provider.BaseColumns;

/**
 * Created by tony on 29/10/16.
 */

public class LazyStatsContract {
    private LazyStatsContract(){}

    private static final String COMMA_SEP = ",";
    private static final String CREATE_TABLE = "CREATE TABLE ";

    public static final String DATABASE_NAME = "LazyStats.db";

    public static class StatCreation implements BaseColumns{
        public static final String TABLE_NAME = "statistics";
        public static final String COL_NAME = "statName";
        public static final String COL_CREATED_BY = "createdBy";// userId of the user
        public static final String COL_REMARK = "remark";
        public static final String COL_TYPE = "type";
        public static final String COL_CREATED_ON = "createdOn";
    }
    public static final String SQL_CREATE_STATS =
            CREATE_TABLE + StatCreation.TABLE_NAME + "(" +
                    StatCreation._ID + " INTEGER PRIMARY KEY"+ COMMA_SEP+
                    StatCreation.COL_NAME + " TEXT UNIQUE NOT NULL"+ COMMA_SEP+
                    StatCreation.COL_REMARK + " TEXT"+ COMMA_SEP+
                    StatCreation.COL_TYPE + " TEXT"+ COMMA_SEP+
                    StatCreation.COL_CREATED_BY + " TEXT"+ COMMA_SEP+
                    StatCreation.COL_CREATED_ON + " TEXT"+")";
    public static final String SQL_DROP_STATS =
            "DROP TABLE IF EXISTS "+StatCreation.TABLE_NAME;
    /*private static final String SQL_INSERT_STATS =
            "INSERT INTO TABLE "+StatCreation.TABLE_NAME;*/
    /*private static final String SQL_DELETE_STATS =
            "DELETE FROM "+StatCreation.TABLE_NAME;*/
}
