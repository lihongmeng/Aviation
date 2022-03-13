package com.jxntv.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.Nullable;

import com.jxntv.widget.GVideoImageView;

public class PublishButton extends GVideoImageView {

    // 点击按钮时是否需要执行旋转动画
    private boolean isNeedClickRotate;

    // 动画是否已经执行，还未还原
    private boolean isRotated;

    // 旋转开始动画
    private RotateAnimation startRotateAnimation;

    // 旋转恢复动画
    private RotateAnimation returnRotateAnimation;

    public PublishButton(Context context) {
        this(context, null);
    }

    public PublishButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        startRotateAnimation = new RotateAnimation(
                0,
                45,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        startRotateAnimation.setDuration(300);
        startRotateAnimation.setFillAfter(true);
        startRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        returnRotateAnimation = new RotateAnimation(
                45,
                0,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        returnRotateAnimation.setDuration(300);
        returnRotateAnimation.setFillAfter(true);
        returnRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void setNeedClickRotate(boolean isNeedClickRotate) {
        if (!isNeedClickRotate && isRotated) {
            startRotate();
        }
        this.isNeedClickRotate = isNeedClickRotate;
    }

    @Override
    public boolean performClick() {
        if (isNeedClickRotate) {
            startRotate();
        }
        return super.performClick();
    }

    public void startRotate() {
        startAnimation(isRotated ? returnRotateAnimation : startRotateAnimation);
        isRotated = !isRotated;
    }

}
