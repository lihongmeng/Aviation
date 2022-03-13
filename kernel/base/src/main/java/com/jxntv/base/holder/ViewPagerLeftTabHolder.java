package com.jxntv.base.holder;

import com.jxntv.base.databinding.ViewPagerLeftTabItemBinding;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.widget.GVideoTextView;

public class ViewPagerLeftTabHolder extends BaseRecyclerViewHolder<ViewPagerLeftTabItemBinding> {

    public final GVideoTextView gVideoTextView;

    public ViewPagerLeftTabHolder(ViewPagerLeftTabItemBinding binding) {
        super(binding);
        gVideoTextView = binding.text;
    }

}
