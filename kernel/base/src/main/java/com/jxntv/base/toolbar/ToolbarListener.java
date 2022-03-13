package com.jxntv.base.toolbar;

import android.view.View;
import androidx.annotation.NonNull;

/**
 * Toolbar 接口
 *
 *
 * @since 2020-02-04 10:05
 */
public interface ToolbarListener {
  /**
   * 当左边返回被点击回调
   *
   * @param view 被点击的视图
   */
  void onLeftBackPressed(@NonNull View view);

  /**
   * 当右边操作按钮被点击回调
   *
   * @param view 被点击的视图
   */
  void onRightOperationPressed(@NonNull View view);
}
