package com.jxntv.android.liteav;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 播放器常量
 */
public class LiteavConstants {
  public static final int TITLE_MODE_FULL = 0;
  public static final int TITLE_MODE_ONLY_BACK = 1;
  public static final int TITLE_MODE_ONLY_TITLE = 2;
  public static final int TITLE_MODE_NONE = 3;
  @IntDef({TITLE_MODE_FULL, TITLE_MODE_ONLY_BACK, TITLE_MODE_ONLY_TITLE, TITLE_MODE_NONE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface TitleMode {}


  public static final int RENDER_MODE_FULL_FILL_SCREEN = 0;
  public static final int RENDER_MODE_ADJUST_RESOLUTION = 1;
  public static final int RENDER_ROTATION_PORTRAIT = 0;
  public static final int RENDER_ROTATION_LANDSCAPE = 270;
  public static final int RENDER_ROTATION_0 = 0;
  public static final int RENDER_ROTATION_90 = 90;
  public static final int RENDER_ROTATION_180 = 180;
  public static final int RENDER_ROTATION_270 = 270;
}
