package com.jxntv.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 首页view pager 去除滑动翻页效果
 */
public class HomeViewPager extends ViewPager {

  public HomeViewPager(Context context) {
    super(context);
  }

  public HomeViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void setCurrentItem(int item) {
    //去除页面切换时的滑动翻页效果
    super.setCurrentItem(item, false);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    // try {
    //   return super.onTouchEvent(ev);
    // } catch (IllegalArgumentException ex) {
    //   ex.printStackTrace();
    // }
    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    // try {
    //   return super.onInterceptTouchEvent(ev);
    // } catch (IllegalArgumentException ex) {
    //   ex.printStackTrace();
    // }
    return false;
  }
}
