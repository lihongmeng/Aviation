package com.jxntv.circle.viewholder;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.circle.databinding.LayoutSelectCircleItemBinding;
import com.jxntv.base.view.MediaImageView;
import com.jxntv.widget.GVideoRImageView;
import com.jxntv.widget.GVideoTextView;

public class SelectCircleHolder extends BaseRecyclerViewHolder<LayoutSelectCircleItemBinding> {

    public final ConstraintLayout root;
    public final GVideoRImageView cover;
    public final GVideoTextView content;

    public SelectCircleHolder(LayoutSelectCircleItemBinding binding) {
        super(binding);
        root = binding.root;
        cover = binding.cover;
        content = binding.content;
    }
}
