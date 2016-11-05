package com.tony.lazystats.fragments;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tony.lazystats.R;
import com.tony.lazystats.model.Statistic;

import java.util.List;

public class MyStatRecyclerViewAdapter extends RecyclerView.Adapter<MyStatRecyclerViewAdapter.ViewHolder> {

    private List<Statistic> mValues;
    private final StatListFragment.OnListFragmentInteractionListener mListener;

    public MyStatRecyclerViewAdapter(List<Statistic> items, StatListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mRemarkView.setText(mValues.get(position).getRemark());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onStatListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRemarkView;
        public Statistic mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mRemarkView = (TextView) view.findViewById(R.id.remark);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRemarkView.getText() + "'";
        }
    }
}
