package com.hzlz.aviation.library.widget.dialog;

import androidx.annotation.NonNull;

/**
 * 弹窗操作回调
 *
 *
 * @since 2020-02-05 20:43
 */
public interface DialogOperationCallback {
  /**
   * 确定回调
   *
   * @param values 每个 Item {@link IDialogItemView} 的值数组
   */
  void onSure(@NonNull Object[] values);

  /**
   * 取消回调
   */
  void onCancel();
}
