package com.hzlz.aviation.kernel.base.model;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * 吐司模型，用于 ViewModel 和 View 之间通信
 *
 *
 * @since 2020-01-07 00:46
 */
public final class ToastModel {
  //<editor-fold desc="属性">
  // 字符串资源 id
  @StringRes
  private int mStringResourceId;
  // 字符串资源对应的参数
  @Nullable
  private Object[] mArguments;
  // 吐司文本
  @Nullable
  private String mText;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  /**
   * @param stringResourceId 字符串资源 id
   * @param arguments 字符串资源对应的参数
   */
  public ToastModel(@StringRes int stringResourceId, @Nullable Object... arguments) {
    mStringResourceId = stringResourceId;
    mArguments = arguments;
  }

  /**
   * @param text 吐司文本
   */
  public ToastModel(@Nullable String text) {
    mText = text;
  }
  //</editor-fold>

  //<editor-fold desc="Getter">

  /**
   * 获取字符串资源 id
   *
   * @return 字符串资源 id
   */
  @StringRes
  public int getStringResourceId() {
    return mStringResourceId;
  }

  /**
   * 获取字符串资源对应的参数
   *
   * @return 字符串资源对应的参数
   */
  @Nullable
  public Object[] getArguments() {
    return mArguments;
  }

  /**
   * 获取文本
   *
   * @return 文本
   */
  @Nullable
  public String getText() {
    return mText;
  }

  //</editor-fold>
}