package com.jxntv.watchtv.adapter;

import static android.view.View.GONE;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.utils.SizeUtils;
import com.jxntv.watchtv.databinding.ItemWatchTvHotTvBinding;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.ArrayList;
import java.util.List;

public class HomeWatchTvHotTvAdapter extends BaseRecyclerAdapter<WatchTvChannel, BaseRecyclerViewHolder> {

    // Activity
    private Activity activity;

    // 数据源
    private List<WatchTvChannel> dataSource;

    // item点击事件
    private OnItemClickListener onItemClickListener;

    public HomeWatchTvHotTvAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        dataSource = new ArrayList<>();
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvHotTvBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvHotTvBinding mBinding = (ItemWatchTvHotTvBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        WatchTvChannel data = dataSource.get(position);
        if (data == null) {
            return;
        }

        mBinding.space.setVisibility(position == 0 ? View.VISIBLE : GONE);

        mBinding.name.setText(data.name);
        mBinding.introduction.setText(data.intro);

        Glide.with(mContext)
                .load(data.picUrl)
                .apply(new RequestOptions().transform(
                        new CenterCrop(),
                        new RoundedCorners(SizeUtils.dp2px(4))
                ))
                .into(mBinding.cover);


        mBinding.root.setTag(data);
        mBinding.root.setOnClickListener(view -> {
            Object tag = view.getTag();
            if (tag == null) {
                return;
            }
            WatchTvChannel watchTvChannel = (WatchTvChannel) tag;
            if (onItemClickListener != null) {
                onItemClickListener.onClick(watchTvChannel);
            }
        });
    }

    public void updateDataSource(List<WatchTvChannel> list) {
        dataSource.clear();
        if (dataSource != null) {
            dataSource.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public interface OnItemClickListener {
        void onClick(WatchTvChannel watchTvChannel);
    }

    public void setItemOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
