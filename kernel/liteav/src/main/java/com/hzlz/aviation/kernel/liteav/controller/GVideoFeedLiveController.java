package com.hzlz.aviation.kernel.liteav.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.hzlz.aviation.kernel.liteav.callback.OnSingleClick;
import com.hzlz.aviation.kernel.liteav.databinding.LiteavControllerFeedLiveBinding;

/**
 * feed流直播类型控制器
 */
public class GVideoFeedLiveController extends GVideoControllerBase {

    private LiteavControllerFeedLiveBinding mBinding;
    private String liveStr;
    private boolean isShowLiveTag;

    public GVideoFeedLiveController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GVideoFeedLiveController(Context context,String liveStr,boolean isShowLiveTag,OnSingleClick listener) {
        super(context);
        this.liveStr = liveStr;
        this.isShowLiveTag = isShowLiveTag;
        this.onSingleClick = listener;
    }

    @Override
    protected View makeControllerView() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = LiteavControllerFeedLiveBinding.inflate(inflater);
        mRoot = mBinding.getRoot();
        mGestureVideoProgressLayout = mBinding.videoProgressLayout;
        mReplay = mBinding.ivReplay;
        mBack = mBinding.ivBack;
        mBinding.liveTypeStr.setText(liveStr);
        mBinding.liveTag.setVisibility(isShowLiveTag?VISIBLE:GONE);
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
                if (onSingleClick !=null) onSingleClick.onClick();
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
