package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.tony.lazystats.R;
import com.tony.lazystats.contract.LazyStatsContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = CreateFragment.class.getSimpleName();

    private EditText mStatName;
    private EditText mStatRemark;
    private String mStatCreatedBy;

    public CreateFragment(){
        //Empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mStatCreatedBy = getArguments().getString("USER_ID");
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
        ContentValues fields = new ContentValues();
        fields.put(LazyStatsContract.Statistics.COL_NAME, statName.toString());
        fields.put(LazyStatsContract.Statistics.COL_REMARK, statRemark.toString());
        // Type of statistics will change in future to SHAREABLE or PERSONAL
        // based on the drop down selected by the user.
        fields.put(LazyStatsContract.Statistics.COL_TYPE, "PERSONAL");
        fields.put(LazyStatsContract.Statistics.COL_CREATED_BY, mStatCreatedBy);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        fields.put(LazyStatsContract.Statistics.COL_CREATED_ON, dateFormat.format(date));
        try {
            getActivity().getContentResolver().insert(ContentUris.withAppendedId(LazyStatsContract.Statistics.CONTENT_URI, 1), fields);
        }catch (SQLiteException e){
            Toast.makeText(getActivity(), "This statistics already exists!! Try with a new name.", Toast.LENGTH_SHORT).show();
        }
        catch (UnsupportedOperationException ex){
            Toast.makeText(getActivity(), "You cannot perform this operation.", Toast.LENGTH_SHORT).show();
        }
    }
}
