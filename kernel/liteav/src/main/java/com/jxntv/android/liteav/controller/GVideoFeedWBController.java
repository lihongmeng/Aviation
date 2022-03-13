package com.jxntv.android.liteav.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.jxntv.android.liteav.callback.OnSingleClick;
import com.jxntv.android.liteav.databinding.LiteavControllerFeedLiveBinding;
import com.jxntv.android.liteav.databinding.LiteavControllerFeedWbBinding;

/**
 * feed流直播类型控制器
 */
public class GVideoFeedWBController extends GVideoControllerBase {

    private LiteavControllerFeedWbBinding mBinding;

    public GVideoFeedWBController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GVideoFeedWBController(Context context , OnSingleClick onSingleClick) {
        super(context);
        this.onSingleClick = onSingleClick;
    }

    @Override
    protected View makeControllerView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = LiteavControllerFeedWbBinding.inflate(inflater);
        mRoot = mBinding.getRoot();
        mGestureVideoProgressLayout = mBinding.videoProgressLayout;
        mReplay = mBinding.ivReplay;
        mBack = mBinding.ivBack;
        initControllerView(mRoot);
        return mRoot;
    }


    @Override
    protected void createGestureDetector() {

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                show();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onSingleClick!=null) onSingleClick.onClick();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX,
                                    float distanceY) {
//                if (downEvent == null || moveEvent == null) {
//                    return false;
//                }
//                if (mVideoGestureUtil != null && mGestureVideoProgressLayout != null && mProgress!=null) {
//                    mVideoGestureUtil.check(mGestureVideoProgressLayout.getHeight(), downEvent, moveEvent,
//                            distanceX, distanceY);
//                }
//                show();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
//                if (mVideoGestureUtil != null && mProgress != null) {
//                    mVideoGestureUtil.reset(getWidth(), mProgress.getProgress());
//                }
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
    }

    public void show(){}

    public void hide(){}


}
