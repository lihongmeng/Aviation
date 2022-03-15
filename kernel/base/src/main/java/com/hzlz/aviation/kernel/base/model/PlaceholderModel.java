package com.hzlz.aviation.kernel.base.model;

import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;

/**
 * 占位布局模型，用于 ViewModel 和 View 之间通信
 *
 *
 * @since 2020-01-07 00:57
 */
public final class PlaceholderModel {
  //<editor-fold desc="属性">
  // 占位布局类型
  @PlaceholderType
  private int mType;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public PlaceholderModel(@PlaceholderType int type) {
    mType = type;
  }
  //</editor-fold>

  //<editor-fold desc="Getter">

  /**
   * 获取占位布局类型 {@link PlaceholderType}
   *
   * @return 占位布局类型
   */
  @PlaceholderType
  public int getType() {
    return mType;
  }

  //</editor-fold>
}
