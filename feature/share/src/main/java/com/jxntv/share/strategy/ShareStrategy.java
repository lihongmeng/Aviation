package com.jxntv.share.strategy;

import com.jxntv.base.model.share.ShareDataModel;

/**
 * 分享策略接口类
 */
public interface ShareStrategy {

  /**
   * 是否可以分享
   */
  public boolean canShare();

  /**
   * 分享
   *
   * @param model 分享数据模型
   */
  public void share(ShareDataModel model);
}
