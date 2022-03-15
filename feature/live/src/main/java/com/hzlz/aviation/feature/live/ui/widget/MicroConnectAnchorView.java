package com.hzlz.aviation.feature.live.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoRImageView;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;
import com.hzlz.aviation.feature.live.R;

public class MicroConnectAnchorView extends ConstraintLayout {

    private Context context;
    public AnchorInfo anchorInfo;
    public boolean isAnchor;

    public GVideoRImageView header;
    public GVideoTextView name;
    public GVideoImageView microStatus;

    public MicroConnectAnchorView(@NonNull Context context) {
        this(context, null);
    }

    public MicroConnectAnchorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicroConnectAnchorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_micro_connect_audience_list, this);
        header = findViewById(R.id.header);
        name = findViewById(R.id.name);
        microStatus = findViewById(R.id.micro_status);
        init();
    }

    public void update(AnchorInfo anchorInfo) {
        if (anchorInfo == null || TextUtils.isEmpty(anchorInfo.userid)) {
            init();
            return;
        }
        if (this.anchorInfo == null || !TextUtils.equals(this.anchorInfo.userAvatar,anchorInfo.userAvatar)){
            Glide.with(context)
                    .load(anchorInfo.userAvatar)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .centerCrop()
                    .into(header);
        }else if (TextUtils.isEmpty(anchorInfo.userAvatar)){
            Glide.with(context)
                    .load(R.drawable.ic_default_avatar)
                    .centerCrop()
                    .into(header);
        }
        this.anchorInfo = anchorInfo;
        name.setText(anchorInfo.userName);
    }

    public void init() {
        this.anchorInfo = null;

        header.setImageResource(R.drawable.icon_join_micro_connect);
        name.setText(isAnchor ?
                ResourcesUtils.getString(R.string.invite_connect_line) :
                ResourcesUtils.getString(R.string.join_connect_micro)
        );

        microStatus.setVisibility(GONE);
    }
}
