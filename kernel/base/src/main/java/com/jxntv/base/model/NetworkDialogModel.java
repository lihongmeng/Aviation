package com.jxntv.base.model;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * 网络请求弹窗模型，用于 ViewModel 和 View 通信
 *
 *
 * @since 2020-01-07 01:07
 */
public final class NetworkDialogModel {
  //<editor-fold desc="属性">
  // 字符串资源 id
  @StringRes
  @Nullable
  private Integer mTextResId;
  @Nullable
  private String mText;
  // 是否显示
  private boolean mIsShow;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public NetworkDialogModel() {
    mIsShow = false;
  }

  /**
   * @param textResId 字符串资源 id
   */
  public NetworkDialogModel(@StringRes int textResId) {
    mTextResId = textResId;
    mIsShow = true;
  }

  /**
   * @param text 字符串资源 id
   */
  public NetworkDialogModel(@Nullable String text) {
    mText = text;
    mIsShow = true;
  }

  //</editor-fold>

  //<editor-fold desc="Getter">

  /**
   * 获取字符串资源 id
   *
   * @return 字符串资源 id
   */
  @StringRes
  @Nullable
  public Integer getTextResId() {
    return mTextResId;
  }

  /**
   * 获取字符串文本
   *
   * @return 字符串文本
   */
  @Nullable
  public String getText() {
    return mText;
  }

  /**
   * 是否显示
   *
   * @return true : 显示 ; false : 不显示
   */
  public boolean isShow() {
    return mIsShow;
  }
  //</editor-fold>
}
