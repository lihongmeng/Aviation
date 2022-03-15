package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import com.hzlz.aviation.library.widget.R;

/**
 * 从底部弹出的弹窗
 *
 *
 * @since 2020-02-04 16:23
 */
public class GVideoBottomSheetDialog extends GVideoDialog {
  //<editor-fold desc="属性">
  protected int mExpectWindowWidth = 0;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public GVideoBottomSheetDialog(Context context) {
    super(context);
    init();
  }

  public GVideoBottomSheetDialog(Context context, int theme) {
    super(context, theme);
    init();
  }

  protected GVideoBottomSheetDialog(Context context, boolean cancelable,
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
      window.setGravity(Gravity.BOTTOM);
      window.getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
    }
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  public void show() {
    super.show();
    Window window = getWindow();
    if (window != null) {
      if (mExpectWindowWidth == 0) {
        mExpectWindowWidth =
            (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.95f);
      }
      window.setLayout(mExpectWindowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
  }
  //</editor-fold>
}
