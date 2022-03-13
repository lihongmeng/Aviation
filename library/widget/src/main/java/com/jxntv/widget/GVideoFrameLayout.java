package com.jxntv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;

/**
 * FrameLayout 基类
 *
 *
 * @since 2020-01-20 11:08
 */
public class GVideoFrameLayout extends FrameLayout {
  //<editor-fold desc="构造函数">
  public GVideoFrameLayout(Context context) {
    super(context);
  }

  public GVideoFrameLayout(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public GVideoFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
