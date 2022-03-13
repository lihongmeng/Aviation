package com.jxntv.base.toolbar;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

/**
 * Toolbar 接口
 *
 *
 * @since 2020-02-04 09:47
 */
public interface IToolbar extends ToolbarAbility {
  /**
   * 获取 Toolbar 的 View
   *
   * @param parent 父控件
   * @return Toolbar 的 View
   */
  @NonNull
  View getView(@NonNull ViewGroup parent);

  /**
   * 获取 Toolbar 高度
   *
   * @return Toolbar 高度
   */
  int getHeight();

  /**
   * 隐藏导航栏
   */
  void hide();
}
