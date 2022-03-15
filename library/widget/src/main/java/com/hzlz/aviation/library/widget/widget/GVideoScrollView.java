package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * ScrollView 基类
 *
 * @since 2020-01-20 11:06
 */
public class GVideoScrollView extends NestedScrollView {

    private OnScrollListener onScrollListener;

    public GVideoScrollView(@NonNull Context context) {
        super(context);
    }

    public GVideoScrollView(@NonNull Context context,
                            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GVideoScrollView(@NonNull Context context, @Nullable AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener == null) {
            return;
        }
        onScrollListener.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
