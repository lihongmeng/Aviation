package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

/**
 * 居中显示的弹窗，默认无动画
 */
public class GVideoCenterDialog extends GVideoDialog {

  //<editor-fold desc="构造函数">
  public GVideoCenterDialog(Context context) {
    super(context);
    init();
  }

  public GVideoCenterDialog(Context context, int theme) {
    super(context, theme);
    init();
  }

  protected GVideoCenterDialog(Context context, boolean cancelable,
                                 OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
    init();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 初始化
   */
  private void init() {
    Window window = getWindow();
    if (window != null) {
      window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      window.setGravity(Gravity.CENTER);
    }
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  protected int getExpectWindowWidth() {
    return ViewGroup.LayoutParams.WRAP_CONTENT;
  }

  protected int getExpectWindowHeight() {
    return ViewGroup.LayoutParams.WRAP_CONTENT;
  }

  @Override
  public void show() {
    super.show();
    Window window = getWindow();
    if (window != null) {
      window.setLayout(getExpectWindowWidth(), getExpectWindowHeight());
    }
  }
  //</editor-fold>
}
