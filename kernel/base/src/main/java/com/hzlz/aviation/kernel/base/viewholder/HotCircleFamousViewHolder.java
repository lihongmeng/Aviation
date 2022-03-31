package com.hzlz.aviation.kernel.base.viewholder;

import com.hzlz.aviation.kernel.base.databinding.ItemHotCircleFamousBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.hzlz.aviation.library.widget.widget.GVideoRImageView;

public class HotCircleFamousViewHolder extends BaseRecyclerViewHolder<ItemHotCircleFamousBinding> {

    private GVideoRImageView header;
    private AviationImageView mark;

    public HotCircleFamousViewHolder(ItemHotCircleFamousBinding binding) {
        super(binding);
        header = binding.header;
        mark = binding.mark;
    }

}
