package com.hzlz.aviation.kernel.base.placeholder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 占位布局接口
 *
 *
 * @since 2020-01-06 23:35
 */
public interface IPlaceholderLayout {

  /**
   * 获取占位布局视图
   *
   * @return 占位布局视图
   */
  @NonNull
  View getView(@NonNull Context context);

  /**
   * 更新占位布局类型
   *
   * @param type        占位布局类型 {@link PlaceholderType}
   * @param isDarkMode  是否为暗黑模式
   * @param paddingTop  上边距
   * @param needShowTop 是否需要顶部显示
   */
  void updateType(@PlaceholderType int type, boolean isDarkMode, int paddingTop, boolean needShowTop);

  /**
   * 设置占位布局接口 {@link PlaceholderListener}
   *
   * @param listener 占位布局接口
   */
  void setPlaceholderListener(@Nullable PlaceholderListener listener);

  /**
   * onDestroy 生命周期
   */
  void onDestroy();
}
