package com.jxntv.dialog;

import androidx.annotation.NonNull;

/**
 * 弹窗 item 视图集合接口
 *
 *
 * @since 2020-02-05 20:58
 */
public interface IDialogItemViewGroup<T> extends IDialogItemView<T> {

  /**
   * 获取子 View
   *
   * @param index 下标
   */
  @NonNull
  IDialogItemView getChild(int index);

  /**
   * 添加 item 视图
   *
   * @param itemView item 视图
   * @param <M> 泛型
   */
  <M> void addItemView(@NonNull IDialogItemView<M> itemView);

  /**
   * 设置回调接口
   *
   * @param callback 回调接口
   */
  void setDialogCallback(@NonNull DialogOperationCallback callback);
}
