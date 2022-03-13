package com.jxntv.base.viewholder;

import com.jxntv.base.databinding.ItemHotCircleFamousBinding;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoRImageView;

public class HotCircleFamousViewHolder extends BaseRecyclerViewHolder<ItemHotCircleFamousBinding> {

    private GVideoRImageView header;
    private GVideoImageView mark;

    public HotCircleFamousViewHolder(ItemHotCircleFamousBinding binding) {
        super(binding);
        header = binding.header;
        mark = binding.mark;
    }

}
