package com.hzlz.aviation.kernel.base.viewholder;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.hzlz.aviation.kernel.base.databinding.ItemHotCircleBinding;
import com.hzlz.aviation.kernel.base.view.CircleTopicLiveLayout;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoRecyclerView;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

public class HotCircleViewHolder extends BaseRecyclerViewHolder<ItemHotCircleBinding> {

    private ConstraintLayout root;
    private GVideoImageView cover;
    private GVideoTextView name;
    private GVideoRecyclerView famousCircleLayout;
    private GVideoImageView joinEnter;
    private CircleTopicLiveLayout circleTopicLive;

    public HotCircleViewHolder(ItemHotCircleBinding binding) {
        super(binding);
        root = binding.root;
        cover = binding.cover;
        name = binding.name;
        famousCircleLayout = binding.famousCircleLayout;
        joinEnter = binding.joinEnter;
        circleTopicLive = binding.circleTopicLive;
    }

}
