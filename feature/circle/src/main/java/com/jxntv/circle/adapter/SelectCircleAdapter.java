package com.jxntv.circle.adapter;

import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_SELECT_CIRCLE;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.circle.BR;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.LayoutSelectCircleItemBinding;
import com.jxntv.circle.viewholder.SelectCircleHolder;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.image.ImageLoaderManager;

import java.util.List;

public class SelectCircleAdapter extends BaseDataBindingAdapter<Circle> {

    private OnItemClickListener onItemClickListener;

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_select_circle_item;
    }

    public void updateDataSource(List<Circle> dataSource) {
        mDataList.clear();
        if (dataSource != null) {
            mDataList.addAll(dataSource);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        Circle circle = mDataList.get(position);
        if(circle==null){
            return;
        }

        LayoutSelectCircleItemBinding binding= (LayoutSelectCircleItemBinding) holder.binding;

        binding.content.setText(circle.name);

        if (circle.imageVO != null && !TextUtils.isEmpty(circle.imageVO.url)) {
            ImageLoaderManager.loadHeadImage(binding.cover,circle.imageVO.url);
        } else {
            binding.cover.setImageResource( R.drawable.ic_default_avatar);
        }

        binding.root.setTag(circle);
        binding.root.setOnClickListener(v -> {
            Object tag = v.getTag();
            if (tag == null||onItemClickListener==null) {
                return;
            }
            onItemClickListener.onItemClick((Circle) tag);
        });
    }

    public interface OnItemClickListener{
        void onItemClick(Circle circle);
    }


}
