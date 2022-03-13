package com.jxntv.watchtv.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.watchtv.R;
import com.jxntv.watchtv.databinding.ItemWatchTvChannelDetailChannelBinding;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.ArrayList;
import java.util.List;

public class WatchTvChannelDetailChannelAdapter extends BaseRecyclerAdapter<WatchTvChannel, BaseRecyclerViewHolder> {

    // Activity
    private Activity activity;

    // 数据源
    private List<WatchTvChannel> dataSource;

    // item点击事件
    private OnItemClickListener onItemClickListener;

    // 当前选中的位置
    private int currentIndex;

    // 初始化状态需要指定选中的频道，通过频道Id去区分
    private long channelId;

    // color_333333
    private final int color333333 = ResourcesUtils.getColor(R.color.color_333333);

    // color_999999
    private final int color7f7f7f = ResourcesUtils.getColor(R.color.color_7f7f7f);

    public WatchTvChannelDetailChannelAdapter(Activity activity, long channelId, List<WatchTvChannel> dataSource) {
        super(activity);
        this.activity = activity;
        this.channelId = channelId;
        this.dataSource = dataSource;
        if (dataSource == null) {
            dataSource = new ArrayList<>();
        }

        int length = dataSource.size();
        currentIndex = 0;
        for (int index = 0; index < length; index++) {
            WatchTvChannel watchTvChannel = dataSource.get(index);
            if (watchTvChannel == null
                    || watchTvChannel.id == null
                    || watchTvChannel.id != channelId) {
                continue;
            }
            currentIndex = index;
        }

        // 防止新数据的容量小于选中的位置
        if (currentIndex > length - 1) {
            currentIndex = 0;
        }

    }

    public void updateDataSource(List<WatchTvChannel> list) {
        dataSource.clear();
        if (dataSource == null) {
            dataSource = new ArrayList<>();
        }
        dataSource.addAll(list);

        // 防止新数据的容量小于选中的位置
        if (currentIndex > dataSource.size() - 1) {
            currentIndex = 0;
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvChannelDetailChannelBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvChannelDetailChannelBinding mBinding = (ItemWatchTvChannelDetailChannelBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        WatchTvChannel data = dataSource.get(position);
        mBinding.name.setText(data.name);

        if (position == currentIndex) {
            mBinding.iconBg.setBackgroundResource(R.drawable.icon_watch_tv_channel_bg);
            mBinding.shadow.setVisibility(VISIBLE);
            mBinding.name.setTextColor(color333333);
        } else {
            mBinding.iconBg.setBackgroundResource(R.drawable.shape_corners_16dp_gradient_ffffff_00ffffff_90);
            mBinding.shadow.setVisibility(GONE);
            mBinding.name.setTextColor(color7f7f7f);
        }

        Glide.with(mContext).load(data.picUrl).into(mBinding.icon);

        mBinding.root.setTag(position);
        mBinding.root.setOnClickListener(view -> {
            Object tag = view.getTag();
            if (tag == null) {
                return;
            }
            int clickPosition = (Integer) tag;
            currentIndex = clickPosition;
            if (onItemClickListener != null) {
                onItemClickListener.onClick(dataSource.get(clickPosition));
            }
            notifyDataSetChanged();
        });

        if (position == 0) {
            mBinding.firstSpace.setVisibility(VISIBLE);
        } else {
            mBinding.firstSpace.setVisibility(GONE);
        }

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
