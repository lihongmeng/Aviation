package com.jxntv.share.widget;

import com.jxntv.share.R;
import com.jxntv.share.ShareRuntime;

/**
 * 分享数据绑定模型
 */
public class ShareDataBind {

  /** 是否为暗黑模式 */
  private boolean mIsDarkMode;

  /**
   * 构造函数
   */
  public ShareDataBind(boolean isDarkMode) {
    mIsDarkMode = isDarkMode;
  }

  /**
   * 获取分享背景色
   *
   * @reutrn 分享背景色
   */
  public int getShareBackgroundColor() {
    int res = mIsDarkMode ? R.color.share_back_color_dark : R.color.share_back_color;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareTitleColor() {
    int res = mIsDarkMode ? R.color.t_color05 : R.color.t_color02;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareIntervalColor() {
    int res = mIsDarkMode ? R.color.share_interval_color_dark : R.color.share_interval_color;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareCancelBackColor() {
    int res = mIsDarkMode ? R.color.share_cancel_back_color_dark : R.color.share_cancel_back_color;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }
}
