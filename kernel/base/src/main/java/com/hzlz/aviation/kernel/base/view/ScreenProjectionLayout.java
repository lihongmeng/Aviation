package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.LayoutScreenProjectionBinding;
import com.hzlz.aviation.library.widget.widget.GVideoConstraintLayout;

/**
 * 投屏后显示投屏状态的布局
 */
public class ScreenProjectionLayout extends GVideoConstraintLayout {

    // 布局文件
    private LayoutScreenProjectionBinding binding;

    // 返回按钮点击事件
    private OnClickListener onBackClickListener;

    // 结束按钮点击事件
    private OnClickListener onEndClickListener;

    public ScreenProjectionLayout(Context context) {
        this(context, null);
    }

    public ScreenProjectionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScreenProjectionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_screen_projection,
                this,
                true
        );

        binding.end.setOnClickListener(v -> {
            if (onEndClickListener == null) {
                return;
            }
            onEndClickListener.onClick(v);
        });

        binding.back.setOnClickListener(v -> {
            if (onBackClickListener == null) {
                return;
            }
            onBackClickListener.onClick(v);
        });

    }


    public void setScreenName(String screenName) {
        binding.name.setText(TextUtils.isEmpty(screenName) ? "" : screenName);
    }

    public void setEndClickListener(OnClickListener onEndClickListener) {
        this.onEndClickListener = onEndClickListener;
    }

    public void setOnBackClickListener(OnClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

}
