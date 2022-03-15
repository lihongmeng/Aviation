package com.hzlz.aviation.kernel.liteav.player;

import android.view.View;

import com.hzlz.aviation.kernel.base.model.video.PendantModel;

/**
 * 播放器事件回掉接口
 */
public interface GVideoPlayerListener {
  /**
   * 播放器已准备完成，可以播放
   */
  default void onPlayPrepared() {
  }

  /**
   * 网络接收到首个可渲染的视频数据包(IDR)
   */
  default void onReceiveFirstFrame() {
  }

  /**
   * 视频播放开始，如果有转菊花什么的这个时候该停了
   */
  default void onPlayBegin() {
  }

  /**
   * 视频播放 loading，如果能够恢复，之后会有 LOADING_END 事件
   */
  default void onPlayLoading() {
  }

  /**
   * 视频播放进度，会通知当前播放进度、加载进度 和总体时长
   */
  default void onProgressChange(int playDuration, int loadingDuration, int duration) {
  }

  /**
   * 视频播放 loading 结束，视频继续播放
   */
  default void onPlayLoadingEnd() {
  }

  /**
   * 视频播放结束
   */
  default void onPlayEnd() {
  }

  /**
   * 网络断连,且经多次重连亦不能恢复,更多重试请自行重启播放
   */
  default void onErrorNetDisconnect() {
  }

  /**
   * 当前视频帧解码失败
   */
  default void onVoiceDecodeFail() {
  }

  /**
   * 当前音频帧解码失
   */
  default void onAudioDecodeFail() {
  }

  /**
   * 网络断连, 已启动自动重连 (重连超过三次就直接抛送 PLAY_ERR_NET_DISCONNECT 了)
   */
  default void onReconnect() {
  }

  /**
   * 播放卡顿
   */
  default void onPlayStuck(){

  }

  /**
   * 视频分辨率改变
   */
  default void onChangeResolution() {
  }

  /**
   * MP4 视频旋转角度
   */
  default void onMp4ChangeRoation() {
  }

  default void onFileNotFound() {
  }

  default void onNetStatus() {
  }

  default boolean interceptFullScreenEvent() { return false; }
  default void onFullscreenChanged(boolean fullscreen) {}

  default void onBackPressed() {}

  default void onControllerShow() {}
  default void onControllerHide() {}
  default void onPlayStateChanged(boolean isPlaying) {}

  default void onPendantShow(PendantModel pendantModel) {}
  default void onPendantClick(PendantModel pendantModel) {}

  default void onShareClick(View view) { }

  default void onScreenProjection(View view) { }

  default void onEndScreenProjection(View view) { }

}
