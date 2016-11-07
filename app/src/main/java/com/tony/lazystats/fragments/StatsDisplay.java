package com.tony.lazystats.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.tony.lazystats.R;
import com.tony.lazystats.contract.LazyStatsContract;
import com.tony.lazystats.model.StatData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsDisplay extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{
    private static final String ARG_STAT_ID = "statId";
    private static final String ARG_STAT_NAME = "statName";

    private String mStatId;
    private String mStatName;
    private LineGraphSeries<DataPoint> series;
    private GraphView graph;
    private EditText mStatDataValue;
    private static final int MAX_DATA_POINT = 100;

    private static final int INSERT_DATA =1;
    private static final int FETCH_ALL =1;

    private OnStatsDisplayInteractionListener mListener;

    public StatsDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param statId Parameter Id of statistic.
     * @param statName Parameter Name of statistic
     * @return A new instance of fragment StatsDisplay.
     */
    public static StatsDisplay newInstance(String statId, String statName) {
        StatsDisplay fragment = new StatsDisplay();
        Bundle args = new Bundle();
        args.putString(ARG_STAT_ID, statId);
        args.putString(ARG_STAT_NAME, statName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStatId = getArguments().getString(ARG_STAT_ID);
            mStatName = getArguments().getString(ARG_STAT_NAME);
        }
        Bundle args = new Bundle();
        args.putString(ARG_STAT_ID, mStatId);
        getLoaderManager().initLoader(FETCH_ALL, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats_display, container, false);
        series = new LineGraphSeries<>();
        graph = (GraphView) view.findViewById(R.id.graphDisp);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        //graph.getGridLabelRenderer().setNumHorizontalLabels();
        graph.addSeries(series);
        mStatDataValue = (EditText) view.findViewById(R.id.editText_dataValue);
        Button btn = (Button) view.findViewById(R.id.btn_LogStat);
        btn.setOnClickListener(this);
        return view;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            StatData sd = new StatData();
            sd.setStatFk(mStatId);
            Editable mStatDataValueText = mStatDataValue.getText();
            if(!TextUtils.isEmpty(mStatDataValueText)) {
                sd.setStatData(Long.valueOf(mStatDataValueText.toString()));
                sd.setCreatedOn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                mListener.onStatsDisplayInteraction(sd, INSERT_DATA);
                mStatDataValueText.clear();
                Bundle args = new Bundle();
                args.putString(ARG_STAT_ID, mStatId);
                getLoaderManager().restartLoader(FETCH_ALL,args,this);
            }
            else{
                Toast.makeText(getActivity(), "Data field is blank! Please enter a value.", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
        void onStatsDisplayInteraction(StatData sd, int clickId);
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
            case FETCH_ALL:
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
        List<DataPoint> statsData = new ArrayList<>();
        long x = 0;
        while (data.moveToNext()){
            /*StatData sd = new StatData();
            sd.setStatDataId(data.getString(data.getColumnIndex(LazyStatsContract.StatsData._ID)));
            sd.setStatData(data.getLong(data.getColumnIndex(LazyStatsContract.StatsData.COL_DATA)));
            sd.setCreatedOn(data.getString(data.getColumnIndex(LazyStatsContract.StatsData.COL_CREATED_ON)));
            statsData.add(sd);*/
            long y = data.getLong(data.getColumnIndex(LazyStatsContract.StatsData.COL_DATA));
            DataPoint dataPoint = new DataPoint(x, y);
            //series.appendData(dataPoint, true, MAX_DATA_POINT);
            statsData.add(dataPoint);
            x++;
        }
        series.resetData(statsData.toArray(new DataPoint[statsData.size()]));
        series.setDrawBackground(true);
        series.setAnimated(true);
        series.setDrawDataPoints(true);
        series.setTitle(mStatName);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(statsData.size());
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v){

        onButtonPressed();
    }
}
