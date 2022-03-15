package com.hzlz.aviation.feature.community.viewholder;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hzlz.aviation.feature.community.databinding.LayoutSelectCircleItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.GVideoRImageView;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

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
