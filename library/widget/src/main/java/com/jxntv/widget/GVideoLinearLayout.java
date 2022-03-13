package com.jxntv.widget;

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
public class GVideoLinearLayout extends LinearLayout {
  //<editor-fold desc="构造函数">
  public GVideoLinearLayout(Context context) {
    super(context);
  }

  public GVideoLinearLayout(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public GVideoLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
