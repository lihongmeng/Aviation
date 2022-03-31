package com.hzlz.aviation.feature.community.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ViewPagerLeftTabItemBinding;
import com.hzlz.aviation.kernel.base.holder.ViewPagerLeftTabHolder;
import com.hzlz.aviation.kernel.base.model.circle.CircleTag;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;

import java.util.List;

public class ViewPagerLeftTabAdapter extends BaseRecyclerAdapter<CircleTag, ViewPagerLeftTabHolder> {

    /**
     * 当前显示的索引
     */
    private int currentIndex = 0;


    public ViewPagerLeftTabAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewPagerLeftTabHolder onCreateVH(ViewGroup parent, int viewType) {
        return new ViewPagerLeftTabHolder(
                ViewPagerLeftTabItemBinding.inflate(mInflater, parent, false)
        );
    }

    @Override
    public void onBindVH(ViewPagerLeftTabHolder viewPagerLeftTabHolder, int position) {
        if (position == currentIndex) {
            viewPagerLeftTabHolder.aviationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
        } else {
            viewPagerLeftTabHolder.aviationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.color_999999));
        }
        viewPagerLeftTabHolder.aviationTextView.setText(mList.get(position).value);
    }

    public void refreshData(@NonNull List<CircleTag> data) {
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }


    public void refreshData(@NonNull List<CircleTag> data, int currentIndex) {
        this.currentIndex = currentIndex;
        mList.clear();
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public void updateIndex(int index) {
        currentIndex = index;
        notifyDataSetChanged();
    }

}
