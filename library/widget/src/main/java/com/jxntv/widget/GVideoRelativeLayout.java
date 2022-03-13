package com.jxntv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;

/**
 * RelativeLayout 基类
 *
 *
 * @since 2020-01-20 11:08
 */
public class GVideoRelativeLayout extends RelativeLayout {
  //<editor-fold desc="构造函数">
  public GVideoRelativeLayout(Context context) {
    super(context);
  }

  public GVideoRelativeLayout(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public GVideoRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
  //</editor-fold>
}
