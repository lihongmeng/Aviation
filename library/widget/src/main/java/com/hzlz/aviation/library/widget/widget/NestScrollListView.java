package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NestScrollListView extends ListView {

    public NestScrollListView(Context context) {
        this(context, null);
    }

    public NestScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST
        );
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
