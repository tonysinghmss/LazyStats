package com.tony.lazystats.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.tony.lazystats.R;
import com.tony.lazystats.contract.LazyStatsContract;
import com.tony.lazystats.model.StatData;

import java.util.ArrayList;
import java.util.List;

public class StatsDisplay extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String ARG_STAT_ID = "statId";
    //private static final String ARG_PARAM2 = "param2";

    private String mStatId;
    //private String mParam2;

    private OnStatsDisplayInteractionListener mListener;

    public StatsDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param statId Parameter Id of statistic.
     * @return A new instance of fragment StatsDisplay.
     */
    public static StatsDisplay newInstance(String statId) {
        StatsDisplay fragment = new StatsDisplay();
        Bundle args = new Bundle();
        args.putString(ARG_STAT_ID, statId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStatId = getArguments().getString(ARG_STAT_ID);
        }
        Bundle args = new Bundle();
        args.putString(ARG_STAT_ID, mStatId);
        getLoaderManager().initLoader(1, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats_display, container, false);
        GraphView graph = (GraphView) view.findViewById(R.id.graphDisp);
        //TODO: Set the adapter for the graph view and use mListener
        return view;
    }

    // Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStatsDisplayInteractionListener) {
            mListener = (OnStatsDisplayInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStatsDisplayInteractionListener {
        void onStatsDisplayInteraction(Uri uri);
    }

    String[] STATS_DATA_PROJECTION = {
            LazyStatsContract.StatsData._ID,
            LazyStatsContract.StatsData.COL_DATA,
            LazyStatsContract.StatsData.COL_CREATED_ON
    };

    String WHERE_CLAUSE = LazyStatsContract.StatsData.COL_STAT_FK+"= ? ";

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        String statFkId = args.getString(ARG_STAT_ID);
        switch (loaderId){
            case 1:
                String[] whereArgs = {statFkId};
                return new CursorLoader(getActivity(),
                        LazyStatsContract.StatsData.CONTENT_URI,
                        STATS_DATA_PROJECTION,
                        WHERE_CLAUSE,                               // Filter clauses
                        whereArgs,                               // Filter args
                        null);                              // Sort order
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<StatData> statsData = new ArrayList<>();
        while (data.moveToNext()){
            StatData sd = new StatData();
            sd.setStatDataId(data.getString(data.getColumnIndex(LazyStatsContract.StatsData._ID)));
            sd.setStatData(data.getLong(data.getColumnIndex(LazyStatsContract.StatsData.COL_DATA)));
            sd.setCreatedOn(data.getString(data.getColumnIndex(LazyStatsContract.StatsData.COL_CREATED_ON)));
            statsData.add(sd);
        }
        //TODO: Set the data using adapter
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
