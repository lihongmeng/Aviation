package com.jxntv.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 波浪指示器控件
 **/
public class WaveIndicatorView extends View {

    private static final float SCALE = 1.0f;
    private float[] scaleYFloats = new float[]{SCALE, SCALE, SCALE, SCALE};
    private Paint mPaint;
    private boolean mHasAnimation;
    private List<Animator> mAnimators;

    private int indicatorWidth;
    private int indicatorSpacing;
    private int indicatorCount;
    private int animDuration = 1000;

    public WaveIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public WaveIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WaveIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void applyAnimation() {
        mAnimators = createAnimation();
    }

    private void init(AttributeSet attrs, int defStyle) {

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.wave_indicator);
        int mIndicatorColor = a.getColor(R.styleable.wave_indicator_indicator_color, Color.WHITE);
        indicatorWidth =  a.getDimensionPixelOffset(R.styleable.wave_indicator_indicator_width, 5);
        indicatorSpacing =  a.getDimensionPixelOffset(R.styleable.wave_indicator_indicator_spacing, 5);
        indicatorCount =  a.getInt(R.styleable.wave_indicator_indicator_count, 3);
        scaleYFloats = new float[indicatorCount];
        Arrays.fill(scaleYFloats, SCALE);
        a.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        bringToFront();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float translateY = getHeight();
        for (int i = 0; i < indicatorCount; i++) {
            canvas.save();
            //设置起始位置
            canvas.translate((i+1) * indicatorWidth + indicatorSpacing * i, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF = new RectF(-indicatorWidth, -getHeight(), 0, 0);
            canvas.drawRoundRect(rectF, 6, 6, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            int dp10 = getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_10DP);
            animDuration = getHeight() / dp10 * 1000;
            applyAnimation();
        }
    }

    @Override
    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
            if (v == GONE || v == INVISIBLE) {
                setAnimationStatus(AnimStatus.END);
            } else {
                setAnimationStatus(AnimStatus.START);
            }
        }
    }

    /**
     * onAttachedToWindow是在第一次onDraw前调用的。也就是我们写的View在没有绘制出来时调用的，但只会调用一次。
     * onDetachedFromWindow相反
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mHasAnimation) {
            setAnimationStatus(AnimStatus.START);
        }
    }

    /**
     * This is called when the view is detached from a window. At this point it no longer has a surface for drawing.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setAnimationStatus(AnimStatus.CANCEL);
    }


    public enum AnimStatus {
        START, END, CANCEL
    }

    /**
     * 设置动画状态
     */
    private void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
            }
        }
    }

    /**
     * 创建动画
     */
    private List<Animator> createAnimation() {
        //indicator 随机设置动画延迟时间
        int[] waveFloats = new int[indicatorCount];
        for (int i = 0; i < indicatorCount; i++) {
//            int intRandom = (int) (1 + Math.random() * (indicatorCount * 2));
            int v = animDuration * i / indicatorCount + 100;
            waveFloats[i] = v;
        }

        List<Animator> animators = new ArrayList<>();
        for (int i = 0; i < indicatorCount; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);
            scaleAnim.setDuration(animDuration);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(waveFloats[i]);
            scaleAnim.addUpdateListener(animation -> {
                scaleYFloats[index] = (float) animation.getAnimatedValue();
                postInvalidate();//刷新界面
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }
}
