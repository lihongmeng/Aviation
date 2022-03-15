package com.hzlz.aviation.kernel.base.placeholder;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 占位布局接口
 *
 *
 * @since 2020-01-07 10:24
 */
public interface PlaceholderListener {
  /**
   * 重新加载
   *
   * @param view 被点击的视图
   */
  void onReload(@NonNull View view);

  /**
   * 登录
   *
   * @param view 被点击的视图
   */
  void onLogin(@NonNull View view);
}
