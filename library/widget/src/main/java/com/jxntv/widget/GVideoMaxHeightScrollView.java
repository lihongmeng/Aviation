package com.jxntv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * ScrollView 基类
 *
 * @since 2020-01-20 11:06
 */
public class GVideoMaxHeightScrollView extends NestedScrollView {

    private int mMaxHeight;

    //<editor-fold desc="构造函数">

    public GVideoMaxHeightScrollView(@NonNull Context context) {
        this(context, null);
    }

    public GVideoMaxHeightScrollView(@NonNull Context context,
                                     @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoMaxHeightScrollView(@NonNull Context context, @Nullable AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    //</editor-fold>

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GVideoMaxHeightScrollView);
        mMaxHeight = typedArray.getLayoutDimension(R.styleable.GVideoMaxHeightScrollView_maxHeight, mMaxHeight);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0 && getHeight() > mMaxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
