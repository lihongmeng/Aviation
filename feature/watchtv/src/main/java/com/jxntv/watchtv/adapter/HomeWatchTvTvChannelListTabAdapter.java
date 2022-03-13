package com.jxntv.watchtv.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.watchtv.R;
import com.jxntv.watchtv.databinding.ItemWatchTvTvChannelListTabBinding;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.ArrayList;
import java.util.List;

public class HomeWatchTvTvChannelListTabAdapter extends BaseRecyclerAdapter<WatchTvChannel, BaseRecyclerViewHolder> {

    // Activity
    private Activity activity;

    // 数据源
    private List<WatchTvChannel> dataSource;

    // item点击事件
    private OnItemClickListener onItemClickListener;

    // 当前选中的位置
    private int currentPosition;

    public HomeWatchTvTvChannelListTabAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        dataSource = new ArrayList<>();
    }

    public void updatePosition(int position) {
        if (currentPosition == position) {
            return;
        }
        currentPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvTvChannelListTabBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvTvChannelListTabBinding mBinding = (ItemWatchTvTvChannelListTabBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        WatchTvChannel watchTvChannel = dataSource.get(position);
        mBinding.name.setText(watchTvChannel.name);

        if (position == currentPosition) {
            mBinding.mask.setVisibility(View.VISIBLE);
            mBinding.name.setTextSize(16);
            mBinding.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mBinding.name.setTextColor(ResourcesUtils.getColor(R.color.color_333333));
        } else {
            mBinding.name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mBinding.mask.setVisibility(View.GONE);
            mBinding.name.setTextSize(14);
            mBinding.name.setTextColor(ResourcesUtils.getColor(R.color.color_999999));
        }

        mBinding.root.setTag(position);
        mBinding.root.setOnClickListener(view -> {
            Object tag = view.getTag();
            if (tag == null) {
                return;
            }
            int clickPosition = (int) tag;
            if (clickPosition == currentPosition) {
                return;
            }

            currentPosition = clickPosition;
            notifyDataSetChanged();

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });

    }

    public void updateDataSource(List<WatchTvChannel> list) {
        dataSource.clear();
        if (dataSource != null) {
            dataSource.addAll(list);
        }
        if (currentPosition >= dataSource.size()) {
            currentPosition = 0;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
