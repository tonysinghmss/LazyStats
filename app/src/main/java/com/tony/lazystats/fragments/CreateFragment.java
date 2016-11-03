package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tony.lazystats.R;
import com.tony.lazystats.dao.LazyStatsContract;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = CreateFragment.class.getSimpleName();
    private static final int CREATE_URL_LOADER = 100;

    private EditText mStatName;
    private EditText mStatRemark;

    private static final String[] PROJECTION =
            {
                    LazyStatsContract.Statistics._ID,
                    LazyStatsContract.Statistics.COL_NAME,
                    LazyStatsContract.Statistics.COL_REMARK,
                    LazyStatsContract.Statistics.COL_CREATED_BY,
                    LazyStatsContract.Statistics.COL_CREATED_ON
            };
    public CreateFragment(){
        //Empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        View createBtn = view.findViewById(R.id.btn_CreateStat);
        createBtn.setOnClickListener(this);
        mStatName = (EditText) view.findViewById(R.id.editText_StatName);
        mStatRemark = (EditText) view.findViewById(R.id.editText_StatRemark);
    }

    @Override
    public void onClick(View v) { //--------------------------->Abstract method of onClickListener
        Log.d(TAG, "Create stats button clicked.");
        Editable statName = mStatName.getText();
        Editable statRemark = mStatRemark.getText();
        Bundle fields = new Bundle();
        fields.putString("stat_name",statName.toString());
        fields.putString("stat_remark",statRemark.toString());
        // Restart the CursorLoader
        getLoaderManager().restartLoader(CREATE_URL_LOADER, fields, this);
        // Step1 : Open database and check the name
        // If name exists show a dialog else save the name and rest all details
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        switch (loaderID){
            case CREATE_URL_LOADER:
                return new CursorLoader(getActivity(),
                        LazyStatsContract.Statistics.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null);
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*
    * Callback that's invoked when the system has initialized the Loader and
    * is ready to start the query. This usually happens when initLoader() is
    * called. The loaderID argument contains the ID value passed to the
    * initLoader() call.
    */
    /*@Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle){
       switch (loaderID){
           case CREATE_URL_LOADER:
               return new CursorLoader(
                       getActivity(),                               //Parent activity context
                       LazyStatsContract.StatCreation.TABLE_NAME,   //Table to query
                       null,     // Projection to return
                       null,            // No selection clause
                       null,            // No selection arguments
                       null             // Default sort order
               );
           default:
               // An invalid id was passed in
               return null;
       }
    }*/
    /*
    Fragment loads LoaderManager –>
    LoaderManager makes a query to ContentProvider –>
    ContentProvider takes instance of DbConnection –>
    DbConnection opens a connection to SQLiteDatabase –>
    SQLiteDatabase initializes SQLiteOpenHelper, creates database if necessary, executes the queries and get Cursor as result –>
    The Cursor will be pushed back to LoaderManager –>
    LoaderManager notifies Activity for showing data.
*/

}
