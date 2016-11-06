package com.tony.lazystats.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tony on 29/10/16.
 */

public class LazyStatsContract {
    private LazyStatsContract(){}

    public static final String DATABASE_NAME = "LazyStatsDatabase";
    public static final int DATABASE_VERSION = 1;

    public static final String SCHEME = "content";
    // The provider's authority
    public static final String AUTHORITY = "com.tony.lazystats.provider";

    public static class Statistics implements BaseColumns{
        public static final String TABLE_NAME = "Statistics";
        public static final String COL_NAME = "statName";
        public static final String COL_REMARK = "remark";
        public static final String COL_TYPE = "type";
        public static final String COL_CREATED_BY = "createdBy";// userId of the user
        public static final String COL_CREATED_ON = "createdOn";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY+"/"+TABLE_NAME);
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+TABLE_NAME;
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd."+AUTHORITY+"."+TABLE_NAME;
    }

    public static class StatsData implements BaseColumns{
        public static final String TABLE_NAME = "StatData";
        public static final String COL_STAT_FK = "statsFkey";
        public static final String COL_DATA = "dataCol";
        public static final String COL_CREATED_ON = "createdOn";
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY+"/"+TABLE_NAME);
        public static final String MIME_TYPE_ROWS =
                "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+TABLE_NAME;
        public static final String MIME_TYPE_SINGLE_ROW =
                "vnd.android.cursor.item/vnd."+AUTHORITY+"."+TABLE_NAME;

    }
}
