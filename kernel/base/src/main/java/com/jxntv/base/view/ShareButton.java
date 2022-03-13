package com.jxntv.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.jxntv.base.R;
import com.jxntv.widget.GVideoImageView;
import com.jxntv.widget.GVideoTextView;

public class ShareButton extends LinearLayout {

    private GVideoTextView nameGtv;
    private GVideoImageView icon;

    private Integer iconDrawableId;
    private String name;

    public ShareButton(Context context) {
        this(context, null);
    }

    public ShareButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShareButton);
            iconDrawableId = array.getResourceId(R.styleable.ShareButton_icon, -1);
            name = array.getString(R.styleable.ShareButton_name);
            array.recycle();
        }
        initViews();
    }

    private void initViews() {
        inflate(getContext(), R.layout.layout_share_button, this);
        nameGtv = findViewById(R.id.name);
        nameGtv.setText(TextUtils.isEmpty(name) ? "" : name);
        icon = findViewById(R.id.icon);
        if (iconDrawableId != 1) {
            icon.setImageResource(iconDrawableId);
        }
    }

    public void setIconImageResource(@DrawableRes int imageResource){
        icon.setImageResource(imageResource);
    }

}
