package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.library.widget.R;

/**
 * ScrollView 基类
 *
 * @since 2020-01-20 11:06
 */
public class GVideoMaxMinHeightRecyclerView extends RecyclerView {

    private int minHeight;

    private int maxHeight;

    //<editor-fold desc="构造函数">

    public GVideoMaxMinHeightRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public GVideoMaxMinHeightRecyclerView(@NonNull Context context,
                                          @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoMaxMinHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
                                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    //</editor-fold>

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GVideoMaxMinHeightRecyclerView);
        maxHeight = typedArray.getLayoutDimension(R.styleable.GVideoMaxMinHeightRecyclerView_max_Height, maxHeight);
        minHeight = typedArray.getLayoutDimension(R.styleable.GVideoMaxMinHeightRecyclerView_min_Height, minHeight);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getHeight();
        if (minHeight > 0 && height < minHeight && minHeight <= maxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(minHeight, MeasureSpec.AT_MOST);
        }
        if (maxHeight > 0 && height > maxHeight && minHeight <= maxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
