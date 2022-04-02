package com.hzlz.aviation.library.widget.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.library.widget.R;
import com.hzlz.aviation.library.widget.databinding.LayoutCountLimitEditLayoutBinding;

public class CountLimitEditLayout extends ConstraintLayout {

    private LayoutCountLimitEditLayoutBinding binding;

    private int editMaxLine;
    private int editMaxCount;
    private boolean showBottomDivider;
    private int bottomDividerColor;
    private int currentContentCount;

    public CountLimitEditLayout(Context context) {
        this(context, null);
    }

    public CountLimitEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountLimitEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountLimitEditLayout);
            editMaxLine = array.getInt(R.styleable.CountLimitEditLayout_edit_max_line, -1);
            editMaxCount = array.getInt(R.styleable.CountLimitEditLayout_edit_max_count, -1);
            showBottomDivider = array.getBoolean(R.styleable.CountLimitEditLayout_show_bottom_divider, false);
            bottomDividerColor = array.getInt(R.styleable.CountLimitEditLayout_bottom_divider_color, ContextCompat.getColor(context, R.color.color_e8e5e5));
            array.recycle();
        }
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.layout_count_limit_edit_layout,
                this,
                true
        );
        if (editMaxLine > 0) {
            binding.content.setMaxLines(editMaxLine);
        }
        binding.content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(editMaxCount)});
        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContentCount = TextUtils.isEmpty(s) ? 0 : s.length();
            }
        });


    }

}
