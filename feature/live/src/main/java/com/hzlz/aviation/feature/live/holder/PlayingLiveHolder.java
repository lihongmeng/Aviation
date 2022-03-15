package com.hzlz.aviation.feature.live.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;
import com.hzlz.aviation.library.widget.widget.WaveIndicatorView;
import com.hzlz.aviation.feature.live.R;

public class PlayingLiveHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout root;
    public GVideoImageView cover;
    public GVideoTextView title;
    public View liveTypeBackground;
    public WaveIndicatorView wave;
    public GVideoTextView liveType;
    public View heraldBackground;
    public GVideoImageView heraldIcon;
    public GVideoTextView heraldTime;
    public View heraldTimeRight;
    public View divider;

    public PlayingLiveHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.root);
        cover = itemView.findViewById(R.id.cover);
        title = itemView.findViewById(R.id.title);
        liveTypeBackground = itemView.findViewById(R.id.live_type_background);
        wave = itemView.findViewById(R.id.wave);
        liveType = itemView.findViewById(R.id.live_type);
        heraldBackground = itemView.findViewById(R.id.herald_background);
        heraldIcon = itemView.findViewById(R.id.herald_icon);
        heraldTime = itemView.findViewById(R.id.herald_time);
        heraldTimeRight = itemView.findViewById(R.id.herald_time_right);
        divider = itemView.findViewById(R.id.divider);
    }

}
