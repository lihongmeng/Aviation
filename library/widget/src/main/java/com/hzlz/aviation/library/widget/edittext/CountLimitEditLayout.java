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

import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.R;
import com.hzlz.aviation.library.widget.databinding.LayoutCountLimitEditLayoutBinding;

public class CountLimitEditLayout extends ConstraintLayout {

    private LayoutCountLimitEditLayoutBinding binding;

    private String editTextHint;
    private int editMaxLine;
    private int editMaxCount;
    private boolean showBottomDivider;
    private int bottomDividerColor;
    private int currentContentCount;
    private float editTextSize;

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
            editTextHint = array.getString(R.styleable.CountLimitEditLayout_edittext_hint);
            editTextSize = array.getDimension(R.styleable.CountLimitEditLayout_edittext_text_size, ResourcesUtils.getDimens(R.dimen.sp_14));
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

        updateCount();

        // 分割线
        binding.bottomDivider.setBackgroundColor(bottomDividerColor);
        binding.bottomDivider.setVisibility(showBottomDivider ? VISIBLE : GONE);

        if (editMaxLine > 0) {
            if (editMaxLine == 1) {
                binding.content.setSingleLine();
            } else {
                binding.content.setMaxLines(editMaxLine);
            }
        }
        binding.content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(editMaxCount)});
        binding.content.setHint(TextUtils.isEmpty(editTextHint)
                ? ResourcesUtils.getString(R.string.please_input_title) : editTextHint);

        binding.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editMaxCount > 0) {
                    String content = TextUtils.isEmpty(s) ? "" : s.toString();
                    currentContentCount = content.length();
                    if (currentContentCount > editMaxCount) {
                        post(() -> binding.content.setText(content.substring(0, editMaxCount)));
                    } else {
                        updateCount();
                    }
                }
            }
        });


    }

    public void updateCount() {
        if (editMaxCount <= 0) {
            editMaxCount = 30;
        }
        binding.contentCount.setText(currentContentCount + "/" + editMaxCount);

    }

}
