package com.jxntv.base.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class AnimUtils {


    /**
     * View渐隐动画效果
     */
    public static void setHideAnimation(final View view, int duration, final AnimationEndListener listener) {
        if (null == view || duration < 0) {
            return;
        }
        view.clearAnimation();
        // 监听动画结束的操作
        AlphaAnimation mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }
        });
        view.startAnimation(mHideAnimation);
    }

    /**
     * View渐现动画效果
     */
    public static void setShowAnimation(final View view, int duration) {
        if (null == view || duration < 0) {
            return;
        }
        view.clearAnimation();
        AlphaAnimation mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
        mShowAnimation.setInterpolator(new LinearInterpolator());
        view.startAnimation(mShowAnimation);
    }


    public interface AnimationEndListener {
        void onAnimationEnd();
    }

    /**
     * View 上滑显示动画效果
     *
     * @param view     动画执行view
     * @param showView 需要显示的view
     * @param duration 动画时长
     * @param y        动画结束时的坐标
     */
    public static void setShowAnimationUp(final View view, final View showView, int duration, float y,
                                          final AnimationEndListener listener) {
        if (null == view || duration < 0) {
            return;
        }
        view.clearAnimation();
        Animation mShowAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, y,
                Animation.RELATIVE_TO_SELF, 0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                if (showView != null) showView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (listener != null) listener.onAnimationEnd();
                view.clearAnimation();
            }
        });
        view.startAnimation(mShowAnimation);
    }

    /**
     * View 下滑隐藏动画效果
     *
     * @param toY   动画开始时的坐标
     * @param fromY 动画结束时的坐标
     */
    public static void setHideAnimationDown(final View view, final View hideView, int duration, float toY,
                                            float fromY, final AnimationEndListener listener) {
        if (null == view || duration < 0) {
            return;
        }
        view.clearAnimation();
        Animation mHideAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, fromY,
                Animation.RELATIVE_TO_SELF, toY);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (hideView != null) hideView.setVisibility(View.GONE);
                if (listener != null) listener.onAnimationEnd();
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(mHideAnimation);
    }


    /**
     * View 左滑进入显示动画效果
     *
     * @param showView 需要显示的view
     * @param duration
     * @param x        轴滑动距离     -1 ~ 0 左进  ， 0 ~ 1 右进
     */
    public static void setShowAnimationLeft(final View showView, int duration, float x, final AnimationEndListener listener) {
        if (null == showView || duration < 0) {
            return;
        }
        showView.clearAnimation();
        Animation mShowAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, x,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                if (showView != null) showView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (listener != null) listener.onAnimationEnd();
                showView.clearAnimation();
            }
        });
        showView.startAnimation(mShowAnimation);
    }

    /**
     * 设置view中心旋转
     * @param view  控件
     */
    public static void setRotateView(View view, int duration){

        view.clearAnimation();
        Animation mShowAnimation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        LinearInterpolator interpolator = new LinearInterpolator();
        mShowAnimation.setDuration(duration);
        mShowAnimation.setRepeatCount(-1);
        mShowAnimation.setInterpolator(interpolator);
        view.startAnimation(mShowAnimation);

    }


}
