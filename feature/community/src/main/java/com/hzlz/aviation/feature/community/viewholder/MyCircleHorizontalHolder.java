package com.hzlz.aviation.feature.community.viewholder;

import com.hzlz.aviation.feature.community.databinding.LayoutMyCircleHorizontalItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.library.widget.widget.AviationImageView;

public class MyCircleHorizontalHolder extends BaseRecyclerViewHolder<LayoutMyCircleHorizontalItemBinding> {

    public final AviationLinearLayout root;
    public final AviationImageView cover;
    public final AviationTextView name;

    public MyCircleHorizontalHolder(LayoutMyCircleHorizontalItemBinding binding) {
        super(binding);
        root = binding.root;
        cover = binding.cover;
        name = binding.name;
    }

}
