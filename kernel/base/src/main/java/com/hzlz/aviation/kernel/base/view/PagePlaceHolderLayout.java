package com.hzlz.aviation.kernel.base.view;

import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY_COMMENT;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY_SEARCH;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.ERROR;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.LOADING;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NONE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.UN_LOGIN;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;

public class PagePlaceHolderLayout extends LinearLayout {

    public int currentType = NONE;
    private Context context;

    private OnClickListener loginOnClickListener;
    private OnClickListener netErrorRetryOnClickListener;

    public PagePlaceHolderLayout(Context context) {
        this(context, null);
    }

    public PagePlaceHolderLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagePlaceHolderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_ffffff));
    }

    public void updatePlaceholderLayoutType(@PlaceholderType int type) {
        if (currentType == type) {
            return;
        }
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        View result = null;
        if (type == NONE) {
            currentType = NONE;
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        switch (type) {
            case LOADING:
                result = LayoutInflater.from(context).inflate(R.layout.placeholder_default_loading, null);
                break;
            case NETWORK_NOT_AVAILABLE:
                result = LayoutInflater.from(context).inflate(R.layout.placeholder_default_network_not_available, null);
                break;
            case EMPTY:
                result = LayoutInflater.from(context).inflate(R.layout.placeholder_default_empty, null);
                break;
            case UN_LOGIN:
                result = LayoutInflater.from(context).inflate(R.layout.placeholder_default_un_login, null);
                break;
            case NONE:
                break;
            case ERROR:
            case EMPTY_COMMENT:
            case EMPTY_SEARCH:
            default:
        }
        if (result != null) {
            addView(result, layoutParams);
        }
        currentType = type;

    }

    public void init(OnClickListener loginOnClickListener, OnClickListener netErrorRetryOnClickListener) {
        this.loginOnClickListener = loginOnClickListener;
        this.netErrorRetryOnClickListener = netErrorRetryOnClickListener;
    }

}
