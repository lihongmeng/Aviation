package com.jxntv.live.holder;

import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.live.databinding.LayoutHomeLivePreviewItemBinding;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoRImageView;
import com.jxntv.widget.GVideoTextView;

public class ReviewLiveHolder extends BaseRecyclerViewHolder<LayoutHomeLivePreviewItemBinding> {

    public RelativeLayout rootLeft;
    public GVideoRImageView coverLeft;
    public GVideoTextView titleLeft;

    public RelativeLayout rootRight;
    public GVideoRImageView coverRight;
    public GVideoTextView titleRight;


    public ReviewLiveHolder(@NonNull LayoutHomeLivePreviewItemBinding binding) {
        super(binding);
        rootLeft = binding.rootLeft;
        coverLeft = binding.coverLeft;
        titleLeft = binding.titleLeft;

        rootRight = binding.rootRight;
        coverRight = binding.coverRight;
        titleRight = binding.titleRight;
    }

}
