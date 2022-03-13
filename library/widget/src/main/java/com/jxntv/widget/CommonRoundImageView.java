package com.jxntv.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆角imageView
 */
public class CommonRoundImageView extends GVideoImageView {

    /** 控件宽度 */
    private float mWidth;
    /** 控件高度 */
    private float mHeight;
    /** 控件角度 */
    private float mConnerSize;
    /** 标记四个角度哪些需要圆角 */
    private boolean mLeftTop;
    private boolean mRightTop;
    private boolean mLeftBottom;
    private boolean mRightBottom;

    /**
     * 构造函数
     */
    public CommonRoundImageView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    /**
     * 构造函数
     */
    public CommonRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造函数
     */
    public CommonRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化，解析conner大小
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonRoundImageView,
                defStyleAttr, 0);
        mConnerSize = ta.getDimension(R.styleable.CommonRoundImageView_corner, 0);
        mLeftBottom = ta.getBoolean(R.styleable.CommonRoundImageView_left_bottom_corner, true);
        mRightBottom = ta.getBoolean(R.styleable.CommonRoundImageView_right_bottom_corner, true);
        mLeftTop = ta.getBoolean(R.styleable.CommonRoundImageView_left_top_corner, true);
        mRightTop = ta.getBoolean(R.styleable.CommonRoundImageView_right_top_corner, true);
        ta.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth >= mConnerSize && mHeight > mConnerSize) {
            Path path = new Path();
            // 四个圆角
            path.moveTo(mConnerSize, 0);
            if (mRightTop) {
                path.lineTo(mWidth - mConnerSize, 0);
                path.quadTo(mWidth, 0, mWidth, mConnerSize);
            } else {
                path.lineTo(mWidth, 0);
            }

            if (mRightBottom) {
                path.lineTo(mWidth, mHeight - mConnerSize);
                path.quadTo(mWidth, mHeight, mWidth - mConnerSize, mHeight);
            } else {
                path.lineTo(mWidth, mHeight);
            }

            if (mLeftBottom) {
                path.lineTo(mConnerSize, mHeight);
                path.quadTo(0, mHeight, 0, mHeight - mConnerSize);
            } else {
                path.lineTo(0, mHeight);
            }

            if (mLeftTop) {
                path.lineTo(0, mConnerSize);
                path.quadTo(0, 0, mConnerSize, 0);
            } else {
                path.lineTo(0, 0);
                path.lineTo(mConnerSize, 0);
            }

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
