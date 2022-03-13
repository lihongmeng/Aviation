package com.jxntv.base.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.jxntv.base.R;
import com.jxntv.base.databinding.LayoutVerticalVideoTitleBinding;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.widget.GVideoTextView;

public class VideoTitleTextView extends GVideoTextView {

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
