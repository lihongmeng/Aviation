package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 *
 * @since 2020-02-17 15:13
 */
public class AviationCoordinatorLayout extends CoordinatorLayout {
  //<editor-fold desc="构造函数">
  public AviationCoordinatorLayout(@NonNull Context context) {
    super(context);
  }

  public AviationCoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public AviationCoordinatorLayout(
      @NonNull Context context,
      @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
