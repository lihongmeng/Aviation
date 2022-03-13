package com.jxntv.android.liteav.controller;

import android.view.View;
import android.widget.MediaController;

import com.jxntv.base.view.ScreenProjectionLayout;

/**
 * 播放器控制器接口
 */
public interface IMediaPlayerControl extends MediaController.MediaPlayerControl {
  //void    start();
  //void    pause();
  //int     getDuration();
  //int     getCurrentPosition();
  //void    seekTo(int pos);
  //boolean isPlaying();
  //int     getBufferPercentage();
  //boolean canPause();
  //boolean canSeekBackward();
  //boolean canSeekForward();
  //int     getAudioSessionId();

  default void onShow() {}
  default void onHide() {}

  boolean canFullscreen();

  boolean isFullScreen();

  /**
   * 切换屏幕的状态
   * 若当前是横屏，就切换竖屏
   * 反之亦然
   */
  void toggleFullScreen();

  /**
   * 和{@link IMediaPlayerControl#toggleFullScreen()}相同的目的
   * 不同的是在横屏的时候是否需要换一个方向横屏
   * needReverse为true代表播放器顶部在左的横屏
   *
   * @param needReverse 是否需要换一个方向横屏
   */
  void toggleFullScreen(boolean needReverse);

  void onBackPressed();

  default void onRetryClicked() {}

  default void onShareClick(View view) { }

  default void onScreenProjection(View view) { }

  default void onEndScreenProjection(View view) { }

}