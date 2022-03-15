package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *
 * @since 2020-02-05 21:34
 */
public abstract class AbstractBottomSheetCompositeDialog extends GVideoBottomSheetDialog
    implements DialogOperationCallback {
  //<editor-fold desc="构造函数">
  public AbstractBottomSheetCompositeDialog(@NonNull Context context) {
    super(context);
  }

  public AbstractBottomSheetCompositeDialog(@NonNull Context context, int theme) {
    super(context, theme);
  }

  protected AbstractBottomSheetCompositeDialog(
      @NonNull Context context,
      boolean cancelable,
      @Nullable OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置 Item 视图集合
   *
   * @param group Item 视图集合
   */
  protected void setDialogItemViewGroup(@NonNull IDialogItemViewGroup group) {
    // 设置接口
    group.setDialogCallback(this);
    // 设置视图
    setContentView(
        group.getView(null, getContext()),
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    );
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  public void onCancel() {
    dismiss();
  }

  //</editor-fold>
}
