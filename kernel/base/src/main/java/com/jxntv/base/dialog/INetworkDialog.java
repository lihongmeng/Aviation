package com.jxntv.base.dialog;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * 网络弹窗接口
 *
 *
 * @since 2020-01-07 01:14
 */
public interface INetworkDialog {

  /**
   * 生命周期 onDestroy
   */
  void onDestroy();

  /**
   * 显示弹窗
   *
   * @param textResId 字符串资源 id
   */
  void show(@StringRes int textResId);

  /**
   * 显示弹窗
   *
   * @param text 字符串
   */
  void show(@Nullable String text);

  /**
   * 隐藏弹窗
   */
  void hideDialog();
}
