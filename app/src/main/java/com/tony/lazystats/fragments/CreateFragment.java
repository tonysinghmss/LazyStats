package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.lazystats.R;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
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
        //Initialize the cursor loader.
        getLoaderManager().initLoader(CREATE_URL_LOADER,null,this);
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
        //TODO: Insert a row with details from this fragment

        // Step1 : Open database and check the name
        // If name exists show a dialog else save the name and rest all details
    }

    private boolean isPresent(){
        //TODO: This will check if the statistics with same name is already present in database.
        return false;
    }

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
