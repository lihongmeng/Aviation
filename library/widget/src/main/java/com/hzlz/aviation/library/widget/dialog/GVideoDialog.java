package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Dialog 基类
 *
 *
 * @since 2020-02-04 18:34
 */
public class GVideoDialog extends AppCompatDialog {
  //<editor-fold desc="构造函数">
  public GVideoDialog(Context context) {
    super(context);
  }

  public GVideoDialog(Context context, int theme) {
    super(context, theme);
  }

  protected GVideoDialog(Context context, boolean cancelable,
      OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }
  //</editor-fold>
}
