package com.jxntv.media;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 自动播放相关常量
 */
public class MediaConstants {
  public static final String EVENT_PENDANT_CLICK = "ev_pendant_click";
  public static final String EVENT_PENDANT_SHOW = "ev_pendant_show";

  /** 超时错误 */
  public static final String ERROR_CASE_TIME_OUT = "error_case_time_out";
  /** 未知错误 */
  public static final String ERROR_CASE_UNKNOWN = "error_case_unknown";
  /** 没有更多 */
  public static final String ERROR_NO_MORE = "error_no_more";
  /** 没有更多 */
  public static final String ERROR_NO_MORE_FROM_REFRESH = "error_no_more_from_refresh";

  /** 分页数据缺省每页拉取10条 */
  public static final int DEFAULT_PAGE_COUNT = 10;

  /** 用于标记全屏的play view tag */
  public static Object playViewTag = new Object();

  /** 播放类型：停止 */
  public static final int STATE_STOP = 0;
  /** 播放类型：暂停  */
  public static final int STATE_PAUSE = 1;
  /** 播放类型：播放  */
  public static final int STATE_PLAY = 2;

  @IntDef({STATE_STOP, STATE_PAUSE, STATE_PLAY})
  @Retention(RetentionPolicy.SOURCE)
  public @interface PlayState {
  }
}
