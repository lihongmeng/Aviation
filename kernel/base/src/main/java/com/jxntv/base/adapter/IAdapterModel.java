package com.jxntv.base.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

/**
 * Adapter 模型
 *
 *
 * @since 2020-02-28 18:29
 */
public interface IAdapterModel {
  /**
   * 设置模型在列表中的位置
   *
   * @param position 模型在列表中的位置
   */
  void setModelPosition(int position);

  /**
   * 获取模型在列表中的位置
   *
   * @return 模型在列表中的位置
   */
  @NonNull
  ObservableInt getModelPosition();
}
