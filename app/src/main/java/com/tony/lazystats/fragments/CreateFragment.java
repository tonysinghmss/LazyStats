package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.lazystats.R;
import com.tony.lazystats.dao.StatCreationDbHelper;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = CreateFragment.class.getSimpleName();
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
    public void onClick(View v) {
        Log.d(TAG, "Create stats button clicked.");
        //TODO: Insert a row with details from this fragment

        // Step1 : Open database and check the name
        // If name exists show a dialog else save the name and rest all details
    }

    private boolean isPresent(){
        //TODO: This will check if the statistics with same name is already present in database.
        return false;
    }

}
