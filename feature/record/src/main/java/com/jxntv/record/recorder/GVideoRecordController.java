package com.jxntv.record.recorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.jxntv.android.liteav.controller.GVideoControllerSmall;

/**
 * 录制预览页面视频播放控制器
 */
public class GVideoRecordController extends GVideoControllerSmall {

  public GVideoRecordController(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GVideoRecordController(Context context) {
    super(context);
  }

  @Override
  protected void createGestureDetector() {
    mGestureDetector =
        new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
          //如果双击的话，则onSingleTapConfirmed不会执行
          @Override
          public boolean onSingleTapConfirmed(MotionEvent e) {
            doPauseResume();
            show();
            return true;
          }

          @Override
          public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX,
                                  float distanceY) {
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
            if (mVideoGestureUtil != null) {
              mVideoGestureUtil.reset(getWidth(), mProgress.getProgress());
            }
            return true;
          }
        });
    mGestureDetector.setIsLongpressEnabled(false);
  }
}
