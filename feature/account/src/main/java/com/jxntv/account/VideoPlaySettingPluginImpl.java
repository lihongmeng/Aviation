package com.jxntv.account;

import com.jxntv.account.presistence.VideoPlaySettingManager;
import com.jxntv.base.plugin.VideoPlaySettingPlugin;

/**
 * 视频播放设置实现
 *
 *
 * @since 2020-02-19 17:08
 */
public final class VideoPlaySettingPluginImpl implements VideoPlaySettingPlugin {
  //<editor-fold desc="方法实现">
  @Override
  public boolean isAutoPlay() {
    return VideoPlaySettingManager.isAutoPlay();
  }

  @Override
  public boolean isAutoPlayOnlyInWifi() {
    return VideoPlaySettingManager.isAutoPlayOnlyInWifi();
  }

  @Override
  public boolean isAutoPlayNextContent() {
    return VideoPlaySettingManager.isAutoPlayNextContent();
  }

  @Override
  public boolean isAutoPlayNextContentOnlyInWifi() {
    return VideoPlaySettingManager.isAutoPlayNextContentOnlyInWifi();
  }
  //</editor-fold>
}
