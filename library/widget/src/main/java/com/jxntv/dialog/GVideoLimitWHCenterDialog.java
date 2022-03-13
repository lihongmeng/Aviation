package com.jxntv.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

/**
 * 居中显示的弹窗，默认无动画
 */
public class GVideoLimitWHCenterDialog extends GVideoDialog {

  protected int mExpectWindowWidth = 0;

  protected int mExpectWindowHeight = 0;

  public GVideoLimitWHCenterDialog(Context context) {
    super(context);
    init();
  }

  public GVideoLimitWHCenterDialog(Context context, int theme) {
    super(context, theme);
    init();
  }

  protected GVideoLimitWHCenterDialog(
          Context context,
          boolean cancelable,
          OnCancelListener cancelListener
  ) {
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
    return mExpectWindowWidth == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mExpectWindowWidth;
  }

  protected int getExpectWindowHeight() {
    return mExpectWindowHeight == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : mExpectWindowHeight;
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
