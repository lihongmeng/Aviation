package com.hzlz.aviation.library.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * 资源工具类
 *
 *
 * @since 2020-02-03 09:43
 */
public final class ResourcesUtils {
  //<editor-fold desc="属性">
  private static Context sApplicationContext;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private ResourcesUtils() {
    throw new IllegalStateException("no instances !!!");
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 初始化
   *
   * @param applicationContext Application 上下文
   */
  public static void init(Context applicationContext) {
    sApplicationContext = applicationContext;
  }

  //<editor-fold desc="字符串">

  /**
   * 获取字符串
   *
   * @param resId 字符串资源 id
   * @param args 参数
   * @return 字符串
   */
  public static String getString(int resId, Object... args) {
    return sApplicationContext.getString(resId, args);
  }
  //</editor-fold>

  //<editor-fold desc="数字">

  /**
   * 获取 int 值
   *
   * @param resId 资源 id
   * @return int 值
   */
  public static int getInt(int resId) {
    return sApplicationContext.getResources().getInteger(resId);
  }
  //</editor-fold>

  //<editor-fold desc="颜色">

  /**
   * 获取颜色
   *
   * @param resId 资源 id
   * @return 颜色
   */
  public static int getColor(int resId) {
    return sApplicationContext.getResources().getColor(resId);
  }

  /**
   * 获取颜色
   *
   * @param resId 资源 id
   * @return 颜色
   */
  public static ColorStateList getColorStateList(int resId) {
    return sApplicationContext.getResources().getColorStateList(resId);
  }
  //</editor-fold>

  //<editor-fold desc="Drawable">

  /**
   * 获取 Drawable
   *
   * @param resId 资源 id
   * @return Drawable
   */
  public static Drawable getDrawable(int resId) {
    return sApplicationContext.getResources().getDrawable(resId);
  }
  //</editor-fold>

  //<editor-fold desc="Dimens">

  /**
   * 获取 Dimens
   *
   * @param resId 资源 id
   * @return Dimens
   */
  public static float getDimens(int resId) {
    return sApplicationContext.getResources().getDimension(resId);
  }

  /**
   * 获取 Dimens
   *
   * @param resId 资源 id
   * @return Dimens
   */
  public static int getIntDimens(int resId) {
    return (int) sApplicationContext.getResources().getDimension(resId);
  }
  //</editor-fold>

  //</editor-fold>
}
