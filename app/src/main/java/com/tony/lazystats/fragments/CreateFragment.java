package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tony.lazystats.R;
import com.tony.lazystats.dao.LazyStatsContract;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = CreateFragment.class.getSimpleName();
    private static final int CREATE_URL_LOADER = 100;

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
    }

    @Override
    public void onClick(View v) { //--------------------------->Abstract method of onClickListener
        Log.d(TAG, "Create stats button clicked.");
        Intent createStatsIntent = new Intent(getString(R.string.action_createStats));
        //TODO: Add details of all fields into this intent
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(createStatsIntent);
        // Step1 : Open database and check the name
        // If name exists show a dialog else save the name and rest all details
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
