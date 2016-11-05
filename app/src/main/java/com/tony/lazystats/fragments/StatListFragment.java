package com.tony.lazystats.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tony.lazystats.R;
import com.tony.lazystats.contract.LazyStatsContract;
import com.tony.lazystats.model.Statistic;

import java.util.ArrayList;
import java.util.List;

public class StatListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = StatListFragment.class.getSimpleName();
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<Statistic> mStatList = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;
    private MyStatRecyclerViewAdapter mRecyclerViewAdapter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StatListFragment() {
    }

    public static StatListFragment newInstance(int columnCount) {
        StatListFragment fragment = new StatListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Inside onCreate");
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        getLoaderManager().initLoader(1,null,this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Inside onCreateView");
        View view = inflater.inflate(R.layout.fragment_stat_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mRecyclerViewAdapter = new MyStatRecyclerViewAdapter(mStatList, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStatListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onStatListFragmentInteraction(Statistic item);
    }

    /* START LoaderManager callback logic */
    String[] STAT_LIST_PROJECTION = {
            LazyStatsContract.Statistics._ID,
            LazyStatsContract.Statistics.COL_NAME,
            LazyStatsContract.Statistics.COL_REMARK,
            LazyStatsContract.Statistics.COL_CREATED_ON
    };

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args){
        Log.d(LOG_TAG, "Inside onCreateLoader");
        switch (loaderId){
            case 1:
                return new CursorLoader(getActivity(), LazyStatsContract.Statistics.CONTENT_URI,
                        STAT_LIST_PROJECTION,               // List of columns to fetch
                        null,                               // Filter clauses
                        null,                               // Filter args
                        null);                              // Sort order
            default:
                return null;

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        Log.d(LOG_TAG, "Inside onLoadFinished");
        if(mStatList==null){
            mStatList = new ArrayList<>();
        }
        while (data.moveToNext()){
            Statistic s = new Statistic();
            s.setStatId(data.getString(data.getColumnIndex(LazyStatsContract.Statistics._ID)));
            s.setName(data.getString(data.getColumnIndex(LazyStatsContract.Statistics.COL_NAME)));
            s.setRemark(data.getString(data.getColumnIndex(LazyStatsContract.Statistics.COL_REMARK)));
            s.setCreatedOn(data.getString(data.getColumnIndex(LazyStatsContract.Statistics.COL_CREATED_ON)));
            Log.d(LOG_TAG, s.getName());
            mStatList.add(s);
        }
        Log.d(LOG_TAG, ""+mStatList.size());
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mStatList = null;
    }
    /* END LoaderManager callbacklogic */

}
