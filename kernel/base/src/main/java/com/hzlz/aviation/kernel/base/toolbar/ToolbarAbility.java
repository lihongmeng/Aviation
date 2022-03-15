package com.hzlz.aviation.kernel.base.toolbar;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Toolbar 功能接口
 *
 *
 * @since 2020-02-04 10:21
 */
public interface ToolbarAbility {
  //<editor-fold desc="背景">

  /**
   * 设置 Toolbar 背景颜色
   *
   * @param color 颜色
   */
  void setToolbarBackgroundColor(@ColorInt int color);
  //</editor-fold>

  //<editor-fold desc="标题">

  /**
   * 设置标题
   *
   * @param resId 文本资源 id
   * @param args 参数
   */
  void setToolbarTitle(@StringRes int resId, Object... args);


  /**
   * 获取标题
   *
   */
  String getToolbarTitle();

  /**
   * 设置标题
   *
   * @param text 文本
   */
  void setToolbarTitle(@NonNull String text);
  //</editor-fold>

  //<editor-fold desc="左边返回文本">

  /**
   * 设置左边返回文本
   *
   * @param resId 文本资源 id
   * @param args 参数
   */
  void setLeftBackText(@StringRes int resId, Object... args);

  /**
   * 设置左边返回按钮文本
   *
   * @param text 文本
   */
  void setLeftBackText(@NonNull String text);
  //</editor-fold>

  //<editor-fold desc="右边操作">

  /**
   * 是否显示右边操作文本
   *
   * @param show true : 显示；false : 不显示
   */
  void showRightOperationTextView(boolean show);

  /**
   * 启用右边操作文本
   *
   * @param enable true : 启用；false : 不启用
   */
  void enableRightOperationTextView(boolean enable);

  /**
   * 设置右边操作文本
   *
   * @param resId 文本资源 id
   * @param args 参数
   */
  void setRightOperationTextViewText(@StringRes int resId, Object... args);


  //<editor-fold desc="右边操作">

  /**
   * 是否显示右边操作
   *
   * @param show true : 显示；false : 不显示
   */
  void showRightOperationImageView(boolean show);

  /**
   * 启用右边操作
   *
   * @param enable true : 启用；false : 不启用
   */
  void enableRightOperationImageView(boolean enable);

  /**
   * 设置右边操作
   *
   * @param resId 图片资源 id
   */
  void setRightOperationImage(@DrawableRes int resId);

  /**
   * 设置右边操作文本
   *
   * @param text 文本
   */
  void setRightOperationTextViewText(@NonNull String text);
  //</editor-fold>

  //<editor-fold desc="监听">

  /**
   * 设置 Toolbar 监听器
   *
   * @param listener Toolbar 监听器 {@link ToolbarListener}
   */
  void setToolbarListener(@NonNull ToolbarListener listener);
  //</editor-fold>
}
