package com.jxntv.watchtv.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.view.ViewGroup;

import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.watchtv.databinding.ItemWatchTvTvChannelBinding;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.ArrayList;
import java.util.List;

public class HomeWatchTvTvChannelAdapter extends BaseRecyclerAdapter<WatchTvChannel, BaseRecyclerViewHolder> {

    // Activity
    private Activity activity;

    // 数据源
    private List<WatchTvChannel> dataSource;

    // item点击事件
    private OnItemClickListener onItemClickListener;

    public HomeWatchTvTvChannelAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        dataSource = new ArrayList<>();
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvTvChannelBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvTvChannelBinding binding = (ItemWatchTvTvChannelBinding) viewHolder.getBinding();
        if (binding == null) {
            return;
        }
        binding.topSpace.setVisibility(position == 0 ? VISIBLE : GONE);
        WatchTvChannel data = dataSource.get(position);
        binding.name.setText(data.name);
        ImageLoaderManager.loadImage(binding.icon, data.picUrl, false);

        binding.root.setTag(data);
        binding.root.setOnClickListener(view -> {
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
