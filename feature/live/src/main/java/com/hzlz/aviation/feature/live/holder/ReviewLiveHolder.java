package com.hzlz.aviation.feature.live.holder;

import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.live.databinding.LayoutHomeLivePreviewItemBinding;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.library.widget.widget.GVideoRImageView;

public class ReviewLiveHolder extends BaseRecyclerViewHolder<LayoutHomeLivePreviewItemBinding> {

    public RelativeLayout rootLeft;
    public GVideoRImageView coverLeft;
    public AviationTextView titleLeft;

    public RelativeLayout rootRight;
    public GVideoRImageView coverRight;
    public AviationTextView titleRight;


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
