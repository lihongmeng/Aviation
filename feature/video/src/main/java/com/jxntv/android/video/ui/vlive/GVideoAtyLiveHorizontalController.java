package com.jxntv.android.video.ui.vlive;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.jxntv.android.liteav.controller.GVideoControllerBase;
import com.jxntv.android.video.databinding.AtyLiveHorizontalControllerBinding;

/**
 * 活动直播横向详情菜单控制器
 */
public class GVideoAtyLiveHorizontalController extends GVideoControllerBase {

    private AtyLiveHorizontalControllerBinding mBinding;
    /**
     * 直播状态
     */
    private boolean isLivingStatus;

    public GVideoAtyLiveHorizontalController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GVideoAtyLiveHorizontalController(Context context, boolean isLivingStatus) {
        super(context);
        this.isLivingStatus = isLivingStatus;
    }

    @Override
    protected View makeControllerView() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = AtyLiveHorizontalControllerBinding.inflate(inflater);
        mRoot = mBinding.getRoot();

        mGestureVideoProgressLayout = mBinding.videoProgressLayout;

        mBack = mBinding.ivBack;

        if (isLivingStatus) {
            mReplay = mBinding.layoutLiveEnd;
            mReplay.setEnabled(false);
            mBinding.layoutBottom.setBackground(null);
        } else {
            mReplay = mBinding.ivReplay;
        }

        initControllerView(mRoot);

        //直播中时，隐藏菜单
        if (isLivingStatus) {
            mPauseButton.setVisibility(GONE);
            mCurrentTime.setVisibility(GONE);
            mProgress.setVisibility(GONE);
            mEndTime.setVisibility(GONE);
            mPauseButton = null;
            mCurrentTime = null;
            mProgress = null;
            mEndTime = null;
        }

        return mRoot;
    }

    @Override
    protected void createGestureDetector() {

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                show();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!isLivingStatus) {
                    toggleControllerView();
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX,
                                    float distanceY) {
                if (downEvent == null || moveEvent == null) {
                    return false;
                }
                if (mVideoGestureUtil != null && mGestureVideoProgressLayout != null && mProgress != null) {
                    mVideoGestureUtil.check(mGestureVideoProgressLayout.getHeight(), downEvent, moveEvent,
                            distanceX, distanceY);
                }
                show();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (mVideoGestureUtil != null && mProgress != null) {
                    mVideoGestureUtil.reset(getWidth(), mProgress.getProgress());
                }
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
    }

    public void showBackBtn() {
        mBinding.ivBack.setVisibility(VISIBLE);
    }

    public void hideBackBtn() {
        mBinding.ivBack.setVisibility(INVISIBLE);
    }

    public void show() {
        super.show();
    }

    public void hide() {
        if (isLivingStatus) {
            super.show();
        }else {
            super.hide();
        }
    }

}
