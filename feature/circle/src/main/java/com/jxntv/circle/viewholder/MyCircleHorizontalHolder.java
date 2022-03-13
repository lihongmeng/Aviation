package com.jxntv.circle.viewholder;

import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.circle.databinding.LayoutMyCircleHorizontalItemBinding;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoLinearLayout;
import com.jxntv.widget.GVideoTextView;

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
