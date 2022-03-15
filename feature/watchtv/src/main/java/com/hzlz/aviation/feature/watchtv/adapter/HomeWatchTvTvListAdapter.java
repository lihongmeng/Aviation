package com.hzlz.aviation.feature.watchtv.adapter;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hzlz.aviation.feature.watchtv.entity.HomeWatchTvTv;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.feature.watchtv.databinding.ItemWatchTvTvBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeWatchTvTvListAdapter extends BaseRecyclerAdapter<HomeWatchTvTv, BaseRecyclerViewHolder> {

    // Activity
    private Activity activity;

    // 数据源
    private List<WatchTvChannel> dataSource = new ArrayList<>();

    // item点击事件
    private OnItemClickListener onItemClickListener;

    public HomeWatchTvTvListAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvTvBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvTvBinding mBinding = (ItemWatchTvTvBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        WatchTvChannel data = dataSource.get(position);

        Glide.with(mContext)
                .load(data.picUrl)
                .apply(new RequestOptions().transform(
                        new CenterCrop(),
                        new RoundedCorners(SizeUtils.dp2px(4))
                ))
                .into(mBinding.tvCover);

        mBinding.tvName.setText(data.name);

        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) mBinding.tvCover.getLayoutParams();
        if (position % 2 == 0) {
            layoutParams.setMarginStart(SizeUtils.dp2px(5));
            layoutParams.setMarginEnd(0);
        } else {
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(SizeUtils.dp2px(5));
        }

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

    public void addDataSource(List<WatchTvChannel> list) {
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
