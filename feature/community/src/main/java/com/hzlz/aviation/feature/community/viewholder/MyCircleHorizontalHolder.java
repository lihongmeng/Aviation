package com.hzlz.aviation.feature.community.viewholder;

import com.hzlz.aviation.feature.community.databinding.LayoutMyCircleHorizontalItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

public class MyCircleHorizontalHolder extends BaseRecyclerViewHolder<LayoutMyCircleHorizontalItemBinding> {

    public final GVideoLinearLayout root;
    public final GVideoImageView cover;
    public final GVideoTextView name;

    public MyCircleHorizontalHolder(LayoutMyCircleHorizontalItemBinding binding) {
        super(binding);
        root = binding.root;
        cover = binding.cover;
        name = binding.name;
    }

}
