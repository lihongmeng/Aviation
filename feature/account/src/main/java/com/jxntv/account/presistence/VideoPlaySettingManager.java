package com.jxntv.account.presistence;

import com.jxntv.base.sp.SharedPrefsWrapper;

/**
 * 视频播放设置管理类
 *
 *
 * @since 2020-02-19 15:30
 */
public final class VideoPlaySettingManager {
  //<editor-fold desc="常量">
  private static final String FILE_NAME_VIDEO_PLAY_SETTING = "video_play_setting";
  private static final SharedPrefsWrapper VIDEO_PLAY_SETTING_SP = new SharedPrefsWrapper(
      FILE_NAME_VIDEO_PLAY_SETTING
  );
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private VideoPlaySettingManager() {

  }
  //</editor-fold>

  //<editor-fold desc="API">

  //<editor-fold desc="自动播放">
  private static final int AUTO_PLAY = 1;
  private static final int AUTO_PLAY_ONLY_IN_WIFI = 2;
  private static final int NOT_AUTO_PLAY = 3;
  private static final String KEY_AUTO_PLAY = "auto_play";
  private static Integer sAutoPlayStatus = null;

  private static void getAutoPlayStatus() {
    if (sAutoPlayStatus == null) {
      sAutoPlayStatus = VIDEO_PLAY_SETTING_SP.getInt(KEY_AUTO_PLAY, AUTO_PLAY);
    }
  }

  /**
   * 是否自动播放
   */
  public static boolean isAutoPlay() {
    getAutoPlayStatus();
    return sAutoPlayStatus == AUTO_PLAY;
  }

  /**
   * 设置自动播放
   */
  public static void setAutoPlay() {
    sAutoPlayStatus = AUTO_PLAY;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY, AUTO_PLAY);
  }

  /**
   * 是否只在 wifi 情况下自动播放
   */
  public static boolean isAutoPlayOnlyInWifi() {
    getAutoPlayStatus();
    return sAutoPlayStatus == AUTO_PLAY_ONLY_IN_WIFI;
  }

  /**
   * 设置只在 wifi 情况下自动播放
   */
  public static void setAutoPlayOnlyInWifi() {
    sAutoPlayStatus = AUTO_PLAY_ONLY_IN_WIFI;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY, AUTO_PLAY_ONLY_IN_WIFI);
  }

  /**
   * 设置不自动播放
   */
  public static void setNotAutoPlay() {
    sAutoPlayStatus = NOT_AUTO_PLAY;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY, NOT_AUTO_PLAY);
  }
  //</editor-fold>

  //<editor-fold desc="自动播放下一个内容">

  private static final int AUTO_PLAY_NEXT_CONTENT = 1;
  private static final int AUTO_PLAY_NEXT_CONTENT_ONLY_IN_WIFI = 2;
  private static final int NOT_AUTO_PLAY_NEXT_CONTENT = 3;
  private static final String KEY_AUTO_PLAY_NEXT_CONTENT = "auto_play_next_content";
  private static Integer sAutoPlayNextContentStatus = null;

  private static void getAutoPlayNextContentStatus() {
    if (sAutoPlayNextContentStatus == null) {
      sAutoPlayNextContentStatus = VIDEO_PLAY_SETTING_SP.getInt(
          KEY_AUTO_PLAY_NEXT_CONTENT, AUTO_PLAY_NEXT_CONTENT
      );
    }
  }

  /**
   * 是否自动播放下一个内容
   */
  public static boolean isAutoPlayNextContent() {
    getAutoPlayNextContentStatus();
    return sAutoPlayNextContentStatus == AUTO_PLAY_NEXT_CONTENT;
  }

  /**
   * 设置自动播放下一个内容
   */
  public static void setAutoPlayNextContent() {
    sAutoPlayNextContentStatus = AUTO_PLAY_NEXT_CONTENT;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY_NEXT_CONTENT, AUTO_PLAY_NEXT_CONTENT);
  }

  /**
   * 是否只在 wifi 情况下自动播放下一个内容
   */
  public static boolean isAutoPlayNextContentOnlyInWifi() {
    getAutoPlayNextContentStatus();
    return sAutoPlayNextContentStatus == AUTO_PLAY_NEXT_CONTENT_ONLY_IN_WIFI;
  }

  /**
   * 设置只在 wifi 情况下自动播放下一个内容
   */
  public static void setAutoPlayNextContentOnlyInWifi() {
    sAutoPlayNextContentStatus = AUTO_PLAY_NEXT_CONTENT_ONLY_IN_WIFI;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY_NEXT_CONTENT, AUTO_PLAY_NEXT_CONTENT_ONLY_IN_WIFI);
  }

  /**
   * 设置不自动播放下一个内容
   */
  public static void setNotAutoPlayNextContent() {
    sAutoPlayNextContentStatus = NOT_AUTO_PLAY_NEXT_CONTENT;
    VIDEO_PLAY_SETTING_SP.putInt(KEY_AUTO_PLAY_NEXT_CONTENT, NOT_AUTO_PLAY_NEXT_CONTENT);
  }
  //</editor-fold>

  //</editor-fold>
}
