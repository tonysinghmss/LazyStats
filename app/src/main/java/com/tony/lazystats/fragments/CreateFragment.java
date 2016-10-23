package com.tony.lazystats.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.lazystats.R;

/**
 * Created by tony on 23/10/16.
 */

public class CreateFragment extends Fragment {
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
}
