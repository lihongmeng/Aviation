package com.jxntv.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.R;
import com.jxntv.base.databinding.LayoutHorizontalVideoTitleBinding;
import com.jxntv.base.databinding.LayoutVerticalVideoTitleBinding;
import com.jxntv.utils.ResourcesUtils;

public class HorizontalVideoTitleLayout extends RelativeLayout {

    private Context context;
    private int contentTextColor;
    private LayoutHorizontalVideoTitleBinding binding;

    public HorizontalVideoTitleLayout(Context context) {
        this(context, null);
    }

    public HorizontalVideoTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalVideoTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalVideoTitleLayout);
            contentTextColor = array.getResourceId(R.styleable.VerticalVideoTitleLayout_content_text_color, -1);
            array.recycle();
        }
        initViews();
    }

    @BindingAdapter("setContentText")
    public static void setContentText(
            @NonNull View view,
            @Nullable String text) {
        ((HorizontalVideoTitleLayout) view).setContentText(text);
    }

    public void setContentText(String content) {
        binding.calculate.setText(content);
        binding.titleScroll.setText(content);
        binding.calculate.post(() -> {
            binding.titleThreeLine.setText(binding.calculate.getText());
            binding.spread.setVisibility(binding.calculate.needShowSpreadButton ? VISIBLE : GONE);
        });
    }


    private void initViews() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_horizontal_video_title,
                this,
                true
        );

        if (contentTextColor > 0) {
            binding.titleThreeLine.setTextColor(ResourcesUtils.getColor(contentTextColor));
            binding.titleScroll.setTextColor(ResourcesUtils.getColor(contentTextColor));
        }

        binding.spread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.titleThreeLine.setVisibility(GONE);
                binding.spread.setVisibility(GONE);
                binding.titleScroll.setVisibility(VISIBLE);
                binding.packUp.setVisibility(VISIBLE);
            }
        });

        binding.packUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.titleThreeLine.setVisibility(VISIBLE);
                binding.spread.setVisibility(VISIBLE);
                binding.titleScroll.setVisibility(GONE);
                binding.packUp.setVisibility(GONE);
            }
        });

    }

}
