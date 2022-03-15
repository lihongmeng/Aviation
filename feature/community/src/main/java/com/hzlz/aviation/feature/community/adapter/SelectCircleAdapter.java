package com.hzlz.aviation.feature.community.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.LayoutSelectCircleItemBinding;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

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
