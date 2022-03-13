package com.jxntv.base.plugin;

import com.jxntv.ioc.Plugin;

/**
 * 视频播放设置
 *
 *
 * @since 2020-02-19 17:01
 */
public interface VideoPlaySettingPlugin extends Plugin {
  //<editor-fold desc="自动播放">

  /**
   * 是否自动播放（不管网络环境）
   *
   * @return true : 自动播放；false : 不自动播放
   */
  boolean isAutoPlay();

  /**
   * 是否仅在 wifi 环境下自动播放
   *
   * @return true : 仅在 wifi 环境下自动播放
   */
  boolean isAutoPlayOnlyInWifi();
  //</editor-fold>

  //<editor-fold desc="自动播放下一个内容">

  /**
   * 是否自动播放下一个内容
   *
   * @return true : 自动播放下一个内容 ; false : 不自动播放下一个内容
   */
  boolean isAutoPlayNextContent();

  /**
   * 是否仅在 wifi 环境下自动播放下一个内容
   *
   * @return true : 仅在 wifi 环境自动播放下一个内容
   */
  boolean isAutoPlayNextContentOnlyInWifi();
  //</editor-fold>
}
