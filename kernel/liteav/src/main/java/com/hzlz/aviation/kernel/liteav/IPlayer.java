package com.hzlz.aviation.kernel.liteav;

import android.view.View;
import android.view.ViewGroup;

import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.liteav.controller.IMediaPlayerControl;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderRotation;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;

/**
 * 播放器接口，各播放器内核实现
 */
public interface IPlayer extends IMediaPlayerControl {

  /**
   * 设置视频播放View
   */
  void setVideoPlayerView(ViewGroup containerView);

  /**
   * 获取视频播放View
   * @return
   */
  View getVideoPlayerView();

  /**
   * 设置播放器回调
   */
  void setKSVideoPlayerListener(GVideoPlayerListener ksVideoPlayerListener);

  /**
   * 无缝切换时使用，从详情页回到当前页面时恢复播放状态；
   */
  boolean restore();

  /**
   * 无缝切换时使用，跳转到详情页之前调用保存当前状态；
   */
  void save();

  /**
   * 清楚无缝切换状态；
   */
  void clear();

  /**
   * 设置是否支持全屏播放（全屏按钮是否展示）
   */
  void setCanFullscreen(boolean canFullscreen);

  /**
   * 设置循环播放
   */
  void setLoop(boolean loop);

  /**
   * 设置静音播放
   * @param mute
   */
  void setMute(boolean mute);


  /**
   * 设置预加载
   */
  void setPreparePlayUrl(String url);


  /**
   * 播放视频，url或者vid
   */
  void startPlay(String url);

  /**
   * 释放播放器资源
   */
  void release();

  /**
   * 恢复播放
   */
  void resume();

  /**
   * 暂停播放
   */
  void pause();

  //void stop();
  //
  //void destroy();

  /**
   * 设置屏幕适配方式
   */
  void setRenderMode(GVideoPlayerRenderMode renderMode);

  /**
   * 设置横竖屏
   */
  void setRenderRotation(GVideoPlayerRenderRotation renderRotation);

  /**
   * 变速播放
   */
  void setPlayerRate(float rate);

  /**
   * 设置采用硬解
   */
  void setHardwareDecode(boolean isHard);

  /**
   * 获取视频宽度
   */
  int getPlayerWidth();

  /**
   * 获取视频高度
   */
  int getPlayerHeight();

  /**
   * 设置埋点数据
   */
  void setStatData(VideoModel videoModel);

  long getPlayProgress();
}
