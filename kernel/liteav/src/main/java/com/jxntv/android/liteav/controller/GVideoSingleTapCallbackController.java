package com.jxntv.android.liteav.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.jxntv.android.liteav.callback.OnSingleClick;

/**
 * 单击播放器不再是显示、隐藏进度条等组件，而是由业务层决定单击效果
 * <p>
 * 可以把本类看作临时处理类，因为{@link com.jxntv.android.liteav.GVideoView}不只是在信息流中使用
 * 详情页也会使用，如果修改{@link GVideoControllerBase#createGestureDetector()}
 * 将单击效果委托给业务层，改动较大，后期可以考虑
 */
@SuppressLint("ViewConstructor")
public class GVideoSingleTapCallbackController extends GVideoControllerSmall {

    public GVideoSingleTapCallbackController(Context context, AttributeSet attrs, OnSingleClick onSingleClick) {
        super(context, attrs);
        this.onSingleClick = onSingleClick;
    }

    public GVideoSingleTapCallbackController(Context context, OnSingleClick onSingleClick) {
        super(context);
        this.onSingleClick = onSingleClick;
    }

    @Override
    protected void createGestureDetector() {
        mGestureDetector =
                new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        doPauseResume();
                        show();
                        return true;
                    }

                    //如果双击的话，则onSingleTapConfirmed不会执行
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (onSingleClick != null) {
                            onSingleClick.onClick();
                        }
                        return true;
                    }

                    @Override
                    public boolean onScroll(
                            MotionEvent downEvent,
                            MotionEvent moveEvent,
                            float distanceX,
                            float distanceY
                    ) {
                        if (downEvent == null || moveEvent == null) {
                            return false;
                        }
                        if (mVideoGestureUtil != null && mGestureVideoProgressLayout != null) {
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
}
