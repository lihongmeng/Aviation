package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.youth.banner.indicator.BaseIndicator;

/**
 * @author huangwei
 * date : 2022/1/6
 * desc :
 **/
public class GVideoBannerIndicator extends BaseIndicator {

    private int mNormalRadius;
    private int mSelectedRadius;
    private int maxRadius;
    private int mShadowRadius = getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_2DP);

    public GVideoBannerIndicator(Context context) {
        this(context, null);
    }

    public GVideoBannerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoBannerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNormalRadius = config.getNormalWidth() / 2;
        mSelectedRadius = config.getSelectedWidth() / 2;
    }

    public GVideoBannerIndicator initData(){
        config.setNormalWidth(getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_4DP));
        config.setNormalColor(ContextCompat.getColor(getContext(), R.color.color_ffffff));
        config.setSelectedWidth(getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_15DP));
        config.setSelectedColor(ContextCompat.getColor(getContext(), R.color.color_e4344e));
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }


        mNormalRadius = config.getNormalWidth() / 2;
        mSelectedRadius = config.getSelectedWidth() / 2;
        //考虑当 选中和默认 的大小不一样的情况
        maxRadius = Math.max(mSelectedRadius, mNormalRadius);
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）+ 阴影*2
        int width = (count - 1) * config.getIndicatorSpace() + config.getSelectedWidth() + config.getNormalWidth() * (count - 1) + mShadowRadius *2;
        setMeasuredDimension(width, Math.max(config.getNormalWidth(), config.getSelectedWidth()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }
        float left = mShadowRadius;
        for (int i = 0; i < count; i++) {
            if (config.getCurrentPosition() != i) {
                mPaint.setShadowLayer(mShadowRadius, 0, 0, ContextCompat.getColor(getContext(), R.color.color_000000_20));
            }else {
                mPaint.clearShadowLayer();
            }
            mPaint.setColor(config.getCurrentPosition() == i ? config.getSelectedColor() : config.getNormalColor());

            int indicatorWidth = config.getCurrentPosition() == i ? config.getSelectedWidth() : config.getNormalWidth();
            int radius = config.getCurrentPosition() == i ? mSelectedRadius : mNormalRadius;

            canvas.drawCircle(left + radius, maxRadius, radius, mPaint);
            left += indicatorWidth + config.getIndicatorSpace();
        }
        //        mPaint.setColor(config.getNormalColor());
        //        for (int i = 0; i < count; i++) {
        //            canvas.drawCircle(left + maxRadius, maxRadius, mNormalRadius, mPaint);
        //            left += config.getNormalWidth() + config.getIndicatorSpace();
        //        }
        //        mPaint.setColor(config.getSelectedColor());
        //        left = maxRadius + (config.getNormalWidth() + config.getIndicatorSpace()) * config.getCurrentPosition();
        //        canvas.drawCircle(left, maxRadius, mSelectedRadius, mPaint);
    }
}
