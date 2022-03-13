package com.jxntv.android.video.ui.vlive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jxntv.android.liteav.controller.GVideoControllerBase;
import com.jxntv.android.video.databinding.AtyLiveVerticalControllerBinding;
import com.jxntv.utils.SizeUtils;

/**
 * 活动直播竖屏全屏情菜单控制器
 */
public class GVideoAtyLiveVerticalController extends GVideoControllerBase {

    private AtyLiveVerticalControllerBinding mBinding;
    /**
     * 直播状态
     */
    private boolean isLivingStatus;
    private boolean mShowProgress;

    public GVideoAtyLiveVerticalController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GVideoAtyLiveVerticalController(Context context, boolean isLivingStatus) {
        super(context);
        this.isLivingStatus = isLivingStatus;
    }

    @Override
    protected View makeControllerView() {
        if (mBinding==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mBinding = AtyLiveVerticalControllerBinding.inflate(inflater);
        }
        mRoot = mBinding.getRoot();

        mSimpleProgress = mBinding.simpleProgress;
        mGestureVideoProgressLayout = mBinding.videoProgressLayout;

        mBack = mBinding.ivBack;

        if (isLivingStatus) {
            mReplay = mBinding.layoutLiveEnd;
            mReplay.setEnabled(false);
            mSimpleProgress = null;
            mRoot.setVisibility(GONE);
        } else {
            mReplay = mBinding.ivReplay;
        }

        initControllerView(mRoot);

        mProgressHeight = SizeUtils.dp2px(40);

        return mRoot;
    }


    @Override
    protected void createGestureDetector() {

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!isLivingStatus) {
                    doPauseResume();
                    show();
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!isLivingStatus) {
                    showProgress(!mShowProgress);
                }
                return true;
            }

        });
        mGestureDetector.setIsLongpressEnabled(false);
    }

    public AtyLiveVerticalControllerBinding getVideoLiveVerticalControllerBinding(){
        if (mBinding==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            mBinding = AtyLiveVerticalControllerBinding.inflate(inflater);
        }
        return mBinding;
    }

    public void show() {
        super.show(-1);
    }

    public void hide(){

    }

    private int mProgressHeight;

    private void showProgress(boolean show) {
        //pauseView && progress 向下动画，
        float startValue = 0.0f;
        if (show) {
        } else {
            startValue = 1.0f - startValue;
        }

        View viewInfo = mBinding.simpleProgress;
        View viewProgress = mBinding.progress;
        viewInfo.clearAnimation();
        viewProgress.clearAnimation();

        ValueAnimator va = ValueAnimator.ofFloat(startValue, 1.0f - startValue);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                viewInfo.setAlpha(1 - value);

                viewProgress.setAlpha(value);
                ViewGroup.LayoutParams params = viewProgress.getLayoutParams();
                params.height = (int) (1.0f * value * mProgressHeight);
                //params.width = (int) ((1.2f - 0.2f * value) * mProgressWidth);
                viewProgress.setLayoutParams(params);
            }
        });
        va.setDuration(400);
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewProgress.setVisibility(View.VISIBLE);
                viewInfo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mShowProgress = show;
                viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                viewInfo.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        va.start();
    }


}
