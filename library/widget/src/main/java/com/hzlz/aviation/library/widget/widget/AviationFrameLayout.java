package com.hzlz.aviation.library.widget.widget;

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
public class AviationFrameLayout extends FrameLayout {
  //<editor-fold desc="构造函数">
  public AviationFrameLayout(Context context) {
    super(context);
  }

  public AviationFrameLayout(Context context,
                             @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AviationFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
