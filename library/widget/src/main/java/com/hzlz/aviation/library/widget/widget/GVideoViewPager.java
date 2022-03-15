package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * ViewPager 基类
 *
 *
 * @since 2020-02-10 14:49
 */
public class GVideoViewPager extends ViewPager {
  //<editor-fold desc="构造函数">
  public GVideoViewPager(@NonNull Context context) {
    super(context);
  }

  public GVideoViewPager(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    try {
      return super.onTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }
    return false;
  }
}
