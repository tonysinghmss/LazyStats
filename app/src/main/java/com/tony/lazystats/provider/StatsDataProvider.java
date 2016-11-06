package com.tony.lazystats.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.tony.lazystats.contract.LazyStatsContract;

/**
 * Created by TONY on 11/3/2016.
 */

public class StatsDataProvider extends ContentProvider{
    public static final String LOG_TAG = StatsDataProvider.class.getSimpleName();
    // Indicates that the incoming query is for Stat creation
    public static final int STATS_LIST = 1;
    public static final int STATS = 2;
    public static final int STATS_DATA_LIST = 3;
    public static final int STATS_DATA = 4;
    public static final int INVALID_URI = -1;

    private static final String COMMA_SEP = ",";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String TEXT_TYPE = "TEXT";
    private static final String INTEGER_TYPE = "INTEGER";
    private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY";
    public static final String SQL_CREATE_STATS =
            CREATE_TABLE + LazyStatsContract.Statistics.TABLE_NAME + "(" +
                    LazyStatsContract.Statistics._ID + " "+PRIMARY_KEY_TYPE+ COMMA_SEP+
                    LazyStatsContract.Statistics.COL_NAME +" "+TEXT_TYPE+" "+"UNIQUE NOT NULL"+ COMMA_SEP+
                    LazyStatsContract.Statistics.COL_REMARK +" "+TEXT_TYPE+ COMMA_SEP+
                    LazyStatsContract.Statistics.COL_TYPE +" "+TEXT_TYPE+ COMMA_SEP+
                    LazyStatsContract.Statistics.COL_CREATED_BY +" "+TEXT_TYPE+ COMMA_SEP+
                    LazyStatsContract.Statistics.COL_CREATED_ON +" "+TEXT_TYPE+")";
    public static final String SQL_DROP_STATS =
            "DROP TABLE IF EXISTS "+ LazyStatsContract.Statistics.TABLE_NAME;
    public static final String SQL_CREATE_STATSDATA =
            CREATE_TABLE + LazyStatsContract.StatsData.TABLE_NAME + "("+
                    LazyStatsContract.StatsData._ID+" "+PRIMARY_KEY_TYPE+COMMA_SEP+
                    LazyStatsContract.StatsData.COL_STAT_FK+" "+INTEGER_TYPE+" "+"NOT NULL"+COMMA_SEP+
                    LazyStatsContract.StatsData.COL_DATA+" "+INTEGER_TYPE+COMMA_SEP+
                    LazyStatsContract.StatsData.COL_CREATED_ON+" "+TEXT_TYPE+")";
    public static final String SQL_DROP_STATS_DATA =
            "DROP TABLE IF EXISTS "+LazyStatsContract.StatsData.TABLE_NAME;

    // Defines an helper object for the backing database
    private SQLiteOpenHelper mHelper;

    // Defines a helper object that matches content URIs to table-specific parameters
    private static final UriMatcher sUriMatcher;

    // Stores the MIME types served by this provider
    private static final SparseArray<String> sMimeTypes;

    /*
     * Initializes meta-data used by the content provider:
     * - UriMatcher that maps content URIs to codes
     * - MimeType array that returns the custom MIME type of a table
     */
    static {

        // Creates an object that associates content URIs with numeric codes
        sUriMatcher = new UriMatcher(0);

        /*
         * Sets up an array that maps content URIs to MIME types, via a mapping between the
         * URIs and an integer code. These are custom MIME types that apply to tables and rows
         * in this particular provider.
         */
        sMimeTypes = new SparseArray<>();

        // Sets up MULTIROW_STATS as code to represent URI for multiple rows of Statistics table
        sUriMatcher.addURI(
                LazyStatsContract.AUTHORITY,
                LazyStatsContract.Statistics.TABLE_NAME,
                STATS_LIST);
        // Sets up UNIROW_STATS as code to represent URI for single row of Statistics table
        sUriMatcher.addURI(
                LazyStatsContract.AUTHORITY,
                LazyStatsContract.Statistics.TABLE_NAME+"/#",
                STATS);
        sUriMatcher.addURI(
                LazyStatsContract.AUTHORITY,
                LazyStatsContract.StatsData.TABLE_NAME,
                STATS_DATA_LIST);
        sUriMatcher.addURI(
                LazyStatsContract.AUTHORITY,
                LazyStatsContract.StatsData.TABLE_NAME+"/#",
                STATS_DATA);

        // Specifies a custom MIME type for a multiple rows of Statistics table
        sMimeTypes.put(
                STATS_LIST,
                LazyStatsContract.Statistics.MIME_TYPE_ROWS);
        // Specifies a custom MIME type for a single row of Statistics table
        sMimeTypes.put(
                STATS,
                LazyStatsContract.Statistics.MIME_TYPE_SINGLE_ROW);
        sMimeTypes.put(
                STATS_DATA_LIST,
                LazyStatsContract.StatsData.MIME_TYPE_ROWS);
        sMimeTypes.put(
                STATS_DATA,
                LazyStatsContract.StatsData.MIME_TYPE_SINGLE_ROW);

    }

    // Closes the SQLite database helper class, to avoid memory leaks
    public void close() {
        mHelper.close();
    }

    private class DataProviderHelper extends SQLiteOpenHelper {
        DataProviderHelper(Context context) {
            super(context,
                    LazyStatsContract.DATABASE_NAME,
                    null,
                    LazyStatsContract.DATABASE_VERSION);
        }

        private void dropTables(SQLiteDatabase db) {
            // If the table doesn't exist, don't throw an error
            db.execSQL(SQL_DROP_STATS_DATA);
            db.execSQL(SQL_DROP_STATS);

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Creates the tables in the backing database for this provider
            db.execSQL(SQL_CREATE_STATS);
            db.execSQL(SQL_CREATE_STATSDATA);
        }
        /**
         * Handles upgrading the database from a previous version. Drops the old tables and creates
         * new ones.
         *
         * @param db The database to upgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Upgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);
        }
        /**
         * Handles downgrading the database from a new to a previous version. Drops the old tables
         * and creates new ones.
         * @param db The database object to downgrade
         * @param version1 The old database version
         * @param version2 The new database version
         */
        @Override
        public void onDowngrade(SQLiteDatabase db, int version1, int version2) {
            Log.w(DataProviderHelper.class.getName(),
                    "Downgrading database from version " + version1 + " to "
                            + version2 + ", which will destroy all the existing data");

            // Drops all the existing tables in the database
            dropTables(db);

            // Invokes the onCreate callback to build new tables
            onCreate(db);

        }
    }
    @Override
    public boolean onCreate() {
        // Creates a new database helper object
        mHelper = new DataProviderHelper(getContext());
        return true;
    }

    /**
     * Returns the result of querying the chosen table.
     * @param uri The content URI of the table
     * @param projection The names of the columns to return in the cursor
     * @param selection The selection clause for the query
     * @param selectionArgs An array of Strings containing search criteria
     * @param sortOrder A clause defining the order in which the retrieved rows should be sorted
     * @return The query results, as a {@link android.database.Cursor} of rows and columns
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "Inside StatsDataProvider query");
        SQLiteDatabase db = mHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)){
            case STATS_LIST:
                if(TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                return db.query(
                        LazyStatsContract.Statistics.TABLE_NAME,    //Table name
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
            case STATS:
                selection = selection + "_ID = "+uri.getLastPathSegment();
                return db.query(
                        LazyStatsContract.Statistics.TABLE_NAME,    //Table name
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
            case STATS_DATA_LIST:
                if(TextUtils.isEmpty(sortOrder)) sortOrder = "_ID ASC";
                return db.query(
                        LazyStatsContract.StatsData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case STATS_DATA:
                selection = selection + "_ID = "+uri.getLastPathSegment();
                return db.query(
                        LazyStatsContract.StatsData.TABLE_NAME,    //Table name
                        projection,                                 //Columns to be shown
                        selection,                                  //Filter clause
                        selectionArgs,                              //Filter arguments
                        null,                                       //group by clause
                        null,                                       //having clause
                        sortOrder                                   //order by clause
                );
            default:
                throw new IllegalArgumentException("Query -- Invalid URI:" + uri);
        }

    }

    /**
     * Returns the mimeType associated with the Uri (query).
     * @see android.content.ContentProvider#getType(Uri)
     * @param uri the content URI to be checked
     * @return the corresponding MIMEtype
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        return sMimeTypes.get(sUriMatcher.match(uri));
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "Inside StatsDataProvider insert");
        switch (sUriMatcher.match(uri)){
            case STATS_LIST:
                // Creates a writeable database or gets one from cache
                SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
                // Inserts the row into the table and returns the new row's _id value
                long id = localSQLiteDatabase.insertOrThrow(
                        LazyStatsContract.Statistics.TABLE_NAME,
                        LazyStatsContract.Statistics.COL_REMARK,//null column hack
                        values
                );
                // If the insert succeeded, notify a change and return the new row's content URI.
                if (-1 != id) {
                    Log.d(LOG_TAG, "Row inserted with id :"+id);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri, Long.toString(id));
                }/* else {
                    throw new UnsupportedOperationException("Failed to insert:" + uri);
                }*/
                break;
            case STATS_DATA_LIST:
                localSQLiteDatabase = mHelper.getWritableDatabase();
                id = localSQLiteDatabase.insert(
                        LazyStatsContract.StatsData.TABLE_NAME,
                        LazyStatsContract.StatsData.COL_DATA,
                        values
                );
                if(-1 != id){
                    Log.d(LOG_TAG, "Row inserted with id :"+id);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return Uri.withAppendedPath(uri,Long.toString(id));
                }
                break;
            default:
                throw new IllegalArgumentException("Insert: Invalid URI" + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "Inside StatsDataprovider delete");
        int deleteCount = 0;
        switch (sUriMatcher.match(uri)){
            case STATS:
                // Creates a writeable database or gets one from cache
                SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
                deleteCount = localSQLiteDatabase.delete(LazyStatsContract.Statistics.TABLE_NAME, selection, selectionArgs);
                break;
            case STATS_DATA_LIST:
                localSQLiteDatabase = mHelper.getWritableDatabase();
                deleteCount = localSQLiteDatabase.delete(LazyStatsContract.StatsData.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Delete multiple rows -- unsupported operation " + uri);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        /*switch (sUriMatcher.match(uri)){
            default:
                throw new IllegalArgumentException("Update: Invalid URI: " + uri);
        }*/
        return -1;
    }


}
