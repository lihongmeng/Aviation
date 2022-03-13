package com.jxntv.base.adapter;

import androidx.annotation.LayoutRes;

/**
 * 数据绑定类型的 Adapter 基类
 *
 *
 * @since 2020-01-16 16:57
 */
public abstract class  BaseDataBindingAdapter<T extends IAdapterModel>
    extends BaseHasPlaceholderMultiItemBindingAdapter<T> {
  //<editor-fold desc="常量">
  private static final int ITEM_TYPE_DEFAULT = 0;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseDataBindingAdapter() {
    addItemType(ITEM_TYPE_DEFAULT, getItemLayoutId());
  }
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 获取 Item View 布局 id
   *
   * @return Item View 布局 id
   */
  @LayoutRes
  protected abstract int getItemLayoutId();
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  protected int getDataItemViewType(int position) {
    if (!mDataList.isEmpty()) {
      return ITEM_TYPE_DEFAULT;
    }
    return super.getDataItemViewType(position);
  }
  //</editor-fold>
}
