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
import com.jxntv.base.databinding.LayoutVerticalVideoTitleBinding;
import com.jxntv.utils.ResourcesUtils;

public class VerticalVideoTitleLayout extends RelativeLayout {

    private Context context;
    private LayoutVerticalVideoTitleBinding binding;
    private int buttonBgResourceId;
    private int contentTextColor;

    public VerticalVideoTitleLayout(Context context) {
        this(context, null);
    }

    public VerticalVideoTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalVideoTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalVideoTitleLayout);
            buttonBgResourceId = array.getResourceId(R.styleable.VerticalVideoTitleLayout_bg_drawable, -1);
            contentTextColor = array.getResourceId(R.styleable.VerticalVideoTitleLayout_content_text_color, -1);
            array.recycle();
        }
        initViews();
    }

    @BindingAdapter("setContentText")
    public static void setContentText(
            @NonNull View view,
            @Nullable String text) {
        ((VerticalVideoTitleLayout) view).setContentText(text);
    }

    public void setContentText(String content) {
        binding.titleScroll.setText(content);
        binding.calculate.setText(content);
        binding.calculate.post(() -> {
            binding.titleThreeLine.setText(binding.calculate.getText());
            binding.spread.setVisibility(binding.calculate.needShowSpreadButton ? VISIBLE : GONE);
        });
    }


    private void initViews() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_vertical_video_title,
                this,
                true
        );

        if (buttonBgResourceId > 0) {
            binding.spread.setBackgroundResource(buttonBgResourceId);
        }

        if (contentTextColor > 0) {
            binding.titleThreeLine.setTextColor(ResourcesUtils.getColor(contentTextColor));
            binding.titleScroll.setTextColor(ResourcesUtils.getColor(contentTextColor));
        }

        binding.spread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.titleThreeLine.setVisibility(GONE);
                binding.spread.setVisibility(GONE);
                binding.titleScrollLayout.setVisibility(VISIBLE);
                binding.packUp.setVisibility(VISIBLE);
            }
        });

        binding.packUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.titleThreeLine.setVisibility(VISIBLE);
                binding.spread.setVisibility(VISIBLE);
                binding.titleScrollLayout.setVisibility(GONE);
                binding.packUp.setVisibility(GONE);
            }
        });

    }

}
