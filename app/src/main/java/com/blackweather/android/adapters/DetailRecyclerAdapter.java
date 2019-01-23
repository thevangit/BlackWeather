package com.blackweather.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blackweather.android.R;
import com.blackweather.android.data.Info;

import java.util.List;

public class DetailRecyclerAdapter extends
        RecyclerView.Adapter<DetailRecyclerAdapter.DetailHolder> {

    private Context mContext;
    private List<Info> mInfoList;

    class DetailHolder extends RecyclerView.ViewHolder {

        private TextView mLabelTextView;
        private TextView mValueTextView;
//        private View mLineView;

        public DetailHolder(@NonNull View itemView) {
            super(itemView);
            mLabelTextView = itemView.findViewById(R.id.detail_item_label);
            mValueTextView = itemView.findViewById(R.id.detail_item_value);
//            mLineView = itemView.findViewById(R.id.detail_item_line);
        }

    }

    public DetailRecyclerAdapter(Context context, List<Info> infoList) {
        super();
        mContext = context;
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public DetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_detail,
                viewGroup, false);
        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHolder detailHolder, int position) {
        Info info = mInfoList.get(position);
        Log.d("tag", "debug_onBindViewHolder_info:" + info + ",position:"
        + position);
        detailHolder.mLabelTextView.setText(info.getLabel());
        detailHolder.mValueTextView.setText(info.getValue());
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    public void swapInfoList(List<Info> infoList) {
        mInfoList = infoList;
        notifyDataSetChanged();
    }
}
