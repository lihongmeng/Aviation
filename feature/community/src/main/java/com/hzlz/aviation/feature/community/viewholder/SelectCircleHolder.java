package com.hzlz.aviation.feature.community.viewholder;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hzlz.aviation.feature.community.databinding.LayoutSelectCircleItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.library.widget.widget.GVideoRImageView;

public class SelectCircleHolder extends BaseRecyclerViewHolder<LayoutSelectCircleItemBinding> {

    public final ConstraintLayout root;
    public final GVideoRImageView cover;
    public final AviationTextView content;

    public SelectCircleHolder(LayoutSelectCircleItemBinding binding) {
        super(binding);
        root = binding.root;
        cover = binding.cover;
        content = binding.content;
    }
}
