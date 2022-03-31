package com.hzlz.aviation.kernel.base.holder;

import com.hzlz.aviation.kernel.base.databinding.ViewPagerLeftTabItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

public class ViewPagerLeftTabHolder extends BaseRecyclerViewHolder<ViewPagerLeftTabItemBinding> {

    public final AviationTextView aviationTextView;

    public ViewPagerLeftTabHolder(ViewPagerLeftTabItemBinding binding) {
        super(binding);
        aviationTextView = binding.text;
    }

}
