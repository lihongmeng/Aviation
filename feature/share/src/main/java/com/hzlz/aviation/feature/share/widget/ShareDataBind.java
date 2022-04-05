package com.hzlz.aviation.feature.share.widget;

import com.hzlz.aviation.feature.share.ShareRuntime;
import com.hzlz.aviation.feature.share.R;

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
    int res = mIsDarkMode ? R.color.color_1b1c1f : R.color.color_f5f6fa;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareTitleColor() {
    int res = mIsDarkMode ? R.color.color_ffffff : R.color.color_525566;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareIntervalColor() {
    int res = mIsDarkMode ? R.color.color_2e2f33 : R.color.color_e6e9f5;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }

  /**
   * 获取分享标题颜色
   *
   * @reutrn 分享标题颜色
   */
  public int getShareCancelBackColor() {
    int res = mIsDarkMode ? R.color.color_1b1b1f : R.color.color_ffffff;
    return ShareRuntime.getAppContext().getResources().getColor(res);
  }
}
