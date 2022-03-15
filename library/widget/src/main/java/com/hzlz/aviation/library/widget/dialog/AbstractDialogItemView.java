package com.hzlz.aviation.library.widget.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 弹窗 item 视图抽象实现
 *
 *
 * @since 2020-02-05 21:03
 */
public abstract class AbstractDialogItemView<T> implements IDialogItemView<T> {
  //<editor-fold desc="属性">
  @Nullable
  private IDialogItemViewGroup mParent;
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  public void setParent(@NonNull IDialogItemViewGroup parent) {
    mParent = parent;
  }

  @Override
  public boolean isValueChanged() {
    return false;
  }

  @Override
  public boolean isCurrentValueValid() {
    return false;
  }

  @Override
  public void onValueChanged(boolean changed) {

  }

  @Override
  public void dispatchValueChanged() {
    if (mParent != null) {
      mParent.dispatchValueChanged();
    }
  }

  @Override
  public void sure() {
    if (mParent != null) {
      mParent.sure();
    }
  }

  @Override
  public void cancel() {
    if (mParent != null) {
      mParent.cancel();
    }
  }
  //</editor-fold>
}
