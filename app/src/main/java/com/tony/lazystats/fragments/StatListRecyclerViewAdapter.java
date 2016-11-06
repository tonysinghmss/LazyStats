package com.tony.lazystats.fragments;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tony.lazystats.R;
import com.tony.lazystats.model.Statistic;

import java.util.List;

public class StatListRecyclerViewAdapter extends RecyclerView.Adapter<StatListRecyclerViewAdapter.ViewHolder> {

    private final List<Statistic> mValues;
    private final StatListFragment.OnListFragmentInteractionListener mListener;
    private static final int ITEM_DETAIL = 1;
    private static final int DELETE_ITEM = 2;

    public StatListRecyclerViewAdapter(List<Statistic> items, StatListFragment.OnListFragmentInteractionListener listener) {
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mRemarkView.setText(mValues.get(position).getRemark());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onStatListFragmentInteraction(holder.mItem, ITEM_DETAIL);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mValues.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                mListener.onStatListFragmentInteraction(holder.mItem, DELETE_ITEM);
                return true;
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
