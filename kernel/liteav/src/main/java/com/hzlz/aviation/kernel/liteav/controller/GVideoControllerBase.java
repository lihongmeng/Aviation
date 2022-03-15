package com.hzlz.aviation.kernel.liteav.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hzlz.aviation.kernel.liteav.R;
import com.hzlz.aviation.kernel.liteav.callback.OnSingleClick;
import com.hzlz.aviation.kernel.liteav.view.ControllerProgressLayout;

/**
 * 控制器基类
 */
public class  GVideoControllerBase extends VideoControllerView {
  private static final int sDefaultTimeout = 6 * 1000;
  private static final int SHOW_TIME_NO_TIME_OUT = -1;

  protected GestureDetector mGestureDetector;
  protected VideoGestureUtil mVideoGestureUtil;
  private boolean mPlayEndState;

  protected ControllerProgressLayout mGestureVideoProgressLayout;
  protected View mReplay;
  protected View mBack;
  protected View share;

  // 单击回调
  protected OnSingleClick onSingleClick;

  public GVideoControllerBase(Context context) {
    super(context);
    init();
  }

  public GVideoControllerBase(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  @Override public void show() {
    super.show(sDefaultTimeout);
  }

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
            toggleControllerView();
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
            if (mVideoGestureUtil != null && mProgress!=null) {
              mVideoGestureUtil.reset(getWidth(), mProgress.getProgress());
            }
            return true;
          }
        });
    mGestureDetector.setIsLongpressEnabled(false);
  }

  /**
   * 覆盖在{@link GVideoControllerBase#init()}中设置的默认VideoGestureListener
   *
   * @param videoGestureListener VideoGestureListener
   */
  public void setVideoGestureListener(VideoGestureUtil.VideoGestureListener videoGestureListener) {
    if (mVideoGestureUtil == null) {
      return;
    }
    mVideoGestureUtil.setVideoGestureListener(videoGestureListener);
  }

  private void init() {
    createGestureDetector();

    mVideoGestureUtil = new VideoGestureUtil(getContext());
    mVideoGestureUtil.setVideoGestureListener(new VideoGestureUtil.VideoGestureListener() {
      @Override
      public void onBrightnessGesture(float newBrightness) {
        if (mGestureVideoProgressLayout != null) {
          mGestureVideoProgressLayout.setProgress((int) (newBrightness * 100));
          mGestureVideoProgressLayout.setImageResource(R.drawable.liteav_light_max);
          mGestureVideoProgressLayout.show();
        }
      }

      @Override
      public void onVolumeGesture(float volumeProgress) {
        if (mGestureVideoProgressLayout != null) {
          mGestureVideoProgressLayout.setImageResource(R.drawable.liteav_volume_max);
          mGestureVideoProgressLayout.setProgress((int) volumeProgress);
          mGestureVideoProgressLayout.show();
        }
      }

      @Override
      public void onSeekGesture(int progress) {
        mDragging = true;
        String currentText = "";
        if (mGestureVideoProgressLayout != null) {

          if (progress > mProgress.getMax()) {
            progress = mProgress.getMax();
          }
          if (progress < 0) {
            progress = 0;
          }
          mGestureVideoProgressLayout.setProgress(progress);
          mGestureVideoProgressLayout.show();

          int duration = mPlayer != null ? mPlayer.getDuration() : 0;
          float percentage = ((float) progress) / mProgress.getMax();
          float currentTime = (duration * percentage);
          String text = stringForTime((int) currentTime) + " / " + stringForTime(duration);
          mGestureVideoProgressLayout.setTimeText(text);
          currentText = stringForTime((int) currentTime);
        }
        if (!TextUtils.isEmpty(currentText)) {
          mCurrentTime.setText(currentText);
        }

        if (mProgress != null) {
          mProgress.setProgress(progress);
        }
        if (mSimpleProgress != null) {
          mSimpleProgress.setProgress(progress);
        }
      }
    });
  }

  @Override protected void initControllerView(View v) {
    super.initControllerView(v);

    mReplay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mPlayer != null) {
          mPlayer.start();
          show();
        }
      }
    });
    mBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mPlayer != null) {
          mPlayer.onBackPressed();
        }
      }
    });
    if(share!=null){
      share.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (mPlayer != null) {
            mPlayer.onShareClick(v);
          }
        }
      });
    }
    if (screenProjection != null) {
      screenProjection.setOnClickListener(view -> {
        if (mPlayer != null) {
          mPlayer.onScreenProjection(v);
        }
      });
    }

    if(screenProjectionController!=null){
      screenProjectionController.setEndClickListener(view -> {
        if (mPlayer != null) {
          mPlayer.onEndScreenProjection(view);
        }
      });
    }

    mGestureVideoProgressLayout.setRetryRunnable(new Runnable() {
      @Override public void run() {
        if (mPlayer != null) {
          mPlayer.onRetryClicked();
        }
      }
    });
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mPlayEndState) {
      return true;
    }
    if (mGestureDetector != null) {
      if (mGestureDetector.onTouchEvent(event)) {
        return true;
      }
    }

    if (event.getAction() == MotionEvent.ACTION_UP &&
            mVideoGestureUtil != null &&
            mVideoGestureUtil.isVideoProgressModel()
            && mProgress != null) {

      int progress = mVideoGestureUtil.getVideoProgress();
      if (progress > mProgress.getMax()) {
        progress = mProgress.getMax();
      }
      if (progress < 0) {
        progress = 0;
      }
      //mProgress.setProgress(progress);

      int seekTime = 0;
      int duration = mPlayer != null ? mPlayer.getDuration() : 0;
      float percentage = progress * 1.0f / mProgress.getMax();
      seekTime = (int) (percentage * duration);
      if (mPlayer != null) {
        mPlayer.seekTo(seekTime);
      }
      //进度调整后，发送消息刷新下方进度条
      show();
      mDragging = false;
    }

    return true;
  }

  @Override public void showTip(String tip, boolean isError) {
    if (mGestureVideoProgressLayout != null) {
      if (TextUtils.isEmpty(tip)) {
        mGestureVideoProgressLayout.setVisibility(View.GONE);
        show(sDefaultTimeout);
      } else {
        mGestureVideoProgressLayout.showTip(tip);
        mGestureVideoProgressLayout.showError(isError);
        show(SHOW_TIME_NO_TIME_OUT);
      }
    }
  }

  @Override public void onReplayShow(boolean show) {
    //展示重播按钮
    if (mReplay != null) {
      mReplay.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @Override public void onPlayEnd(boolean showReplay) {
    //进入重播状态，一直展示controller
    mPlayEndState = showReplay;
    if (showReplay) {
      show(SHOW_TIME_NO_TIME_OUT);
    }
    //隐藏其他按钮
    if (mProgress != null) {
      mProgress.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
    if (mSimpleProgress != null) {
      mSimpleProgress.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
    if (mPauseButton != null) {
      mPauseButton.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
    if (mCurrentTime != null) {
      mCurrentTime.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
    if (mEndTime != null) {
      mEndTime.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
    View fullscreenButton=getFullscreenButton();
    if (fullscreenButton != null && mPlayer != null && mPlayer.canFullscreen()) {
      fullscreenButton.setVisibility(showReplay ? View.GONE : View.VISIBLE);
    }
  }

  //滑动进度条时，不展示屏幕中间滑动窗口
  //@Override
  //public void onProgressChanged(TCPointSeekBar seekBar, int progress, boolean isFromUser) {
  //  if (mGestureVideoProgressLayout != null && isFromUser) {
  //    mGestureVideoProgressLayout.show();
  //    float percentage = ((float) progress) / seekBar.getMax();
  //    float currentTime = (mVodController.getDuration() * percentage);
  //      mGestureVideoProgressLayout.setTimeText(TCTimeUtils.formattedTime((long) currentTime) + " / " + TCTimeUtils.formattedTime((long) mVodController.getDuration()));
  //    mGestureVideoProgressLayout.setProgress(progress);
  //  }
  //}
}
