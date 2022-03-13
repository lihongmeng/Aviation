package com.jxntv.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.jxntv.base.R;
import com.jxntv.utils.DateUtils;
import com.jxntv.widget.GVideoTextView;

public class LiveStartTimeView extends ConstraintLayout {

    private GVideoTextView timeView;

    public LiveStartTimeView(@NonNull Context context) {
        this(context, null);
    }

    public LiveStartTimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveStartTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_live_start_time_view, this);
        timeView = findViewById(R.id.herald_time);
    }

    public void setLongTime(Long time) {
        if (time == null) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            timeView.setText(DateUtils.changeTimeLongToString(time, "MM/dd HH:mm"));
        }
    }

}
