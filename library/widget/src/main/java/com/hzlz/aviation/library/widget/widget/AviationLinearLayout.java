package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * LinearLayout 基类
 *
 *
 * @since 2020-01-20 11:08
 */
public class AviationLinearLayout extends LinearLayout {
  //<editor-fold desc="构造函数">
  public AviationLinearLayout(Context context) {
    super(context);
  }

  public AviationLinearLayout(Context context,
                              @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AviationLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
