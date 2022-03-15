package com.hzlz.aviation.kernel.media;

import com.hzlz.aviation.kernel.base.plugin.VideoPlaySettingPlugin;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * 自动播放配置
 */
public class MediaConfig {

  /**
   * feed自动播放大小
   */
  public static float AUTO_PLAY_SCREEN_RATIO = 1.0f / 4.0f;

  /**
   * 是否支持自动播放
   *
   * @return 是否支持自动播放
   */
  public static boolean isSupportAutoPlay() {
    if (PluginManager.get(VideoPlaySettingPlugin.class).isAutoPlayOnlyInWifi()
        && NetworkUtils.isWifiNetworkConnected()) {
      return true;
    }

    if (PluginManager.get(VideoPlaySettingPlugin.class).isAutoPlay()) {
      return true;
    }
    return false;
  }

  /**
   * 是否支持自动播放下一个
   *
   * @return 是否支持自动播放下一个
   */
  public static boolean isSupportAutoPlayNext() {
    if (PluginManager.get(VideoPlaySettingPlugin.class).isAutoPlayNextContentOnlyInWifi()
        && NetworkUtils.isWifiNetworkConnected()) {
      return true;
    }

    if (PluginManager.get(VideoPlaySettingPlugin.class).isAutoPlayNextContent()) {
      return true;
    }

    return false;
  }
}
