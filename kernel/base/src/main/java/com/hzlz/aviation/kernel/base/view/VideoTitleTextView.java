package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.util.AttributeSet;

import com.hzlz.aviation.kernel.base.databinding.LayoutVerticalVideoTitleBinding;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

public class VideoTitleTextView extends AviationTextView {

    private Context context;
    private LayoutVerticalVideoTitleBinding binding;

    public VideoTitleTextView(Context context) {
        this(context, null);
    }

    public VideoTitleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

}
