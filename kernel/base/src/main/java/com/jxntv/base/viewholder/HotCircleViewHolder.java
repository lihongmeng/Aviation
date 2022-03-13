package com.jxntv.base.viewholder;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.jxntv.base.databinding.ItemHotCircleBinding;
import com.jxntv.base.view.CircleTopicLiveLayout;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoLinearLayout;
import com.jxntv.widget.GVideoRecyclerView;
import com.jxntv.widget.GVideoTextView;

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
