package com.hzlz.aviation.kernel.stat.stat;

/**
 * 埋点相关常量
 */
public class StatConstants {
  public static final String EV_APP_START = "start";
  public static final String EV_APP_EXIT = "exit";
  public static final String EV_SPLASH = "splash";
  public static final String EV_ADVERT = "advert";
  public static final String EV_SHARE = "share";
  public static final String EV_COMMENT = "comment";
  public static final String EV_FAVORITE = "favorite";
  public static final String EV_PLAY = "play";
  public static final String EV_MEDIA = "media";

  public static final String DS_KEY_EXTEND_ID = "extend_id";
  public static final String DS_KEY_EXTEND_NAME = "extend_name";
  public static final String DS_KEY_EXTEND_SHOW_TYPE = "extend_show_type";
  public static final String DS_KEY_PLACE = "place";
  public static final String DS_KEY_IS_CLICK = "is_click";
  public static final String DS_KEY_METHOD = "method";
  public static final String DS_KEY_MODULE = "module";
  public static final String DS_KEY_COMMENT = "comment";
  public static final String DS_KEY_COMMENT_TYPE = "comment_type";
  public static final String DS_KEY_FAVORITE = "favorite";
  public static final String DS_KEY_PLAY_DURATION = "play_duration";
  public static final String DS_KEY_CHANGED_PROGRESS = "changed_progress";
  public static final String DS_KEY_VIDEO_TIME = "video_time";

  public static final String DS_KEY_IS_FINISH = "is_finish";
  public static final String DS_KEY_PID = "pid";
  public static final String DS_KEY_CONTENT_ID = "content_id";
  public static final String DS_KEY_CHANNEL_ID = "channel_id";
  public static final String DS_KEY_FROM = "from";


  public static final String DS_KEY_PLACE_ADVERT = "1";
  public static final String DS_KEY_PLACE_PENDANT = "2";

  /** 单次缓存埋点最大条数 */
  public static final int STAT_MAX_COUNT = 100;


  /** 主动点击 */
  public static final String TYPE_CLICK_C = "c";
  /** 曝光 */
  public static final String TYPE_SHOW_E = "e";
  /** 页面曝光 */
  public static final String TYPE_PAGE_W = "w";
  /** 页面退出 */
  public static final String TYPE_PAGE_Q = "q";
  /** App生命周期变化 */
  public static final String TYPE_APP_A = "a";

  /** App生命周期-冷启动 */
  public static final String APP_START = "start";
  /** App生命周期-热启动 */
  public static final String APP_ACTIVE = "active";
  /** App生命周期-Home到后台 */
  public static final String APP_BACKGROUND = "background";
  /** App生命周期-退出 */
  public static final String APP_EXIT = "exit";
}
