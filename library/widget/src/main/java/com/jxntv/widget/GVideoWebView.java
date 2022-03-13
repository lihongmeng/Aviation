package com.jxntv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * WebView 基类
 *
 *
 * @since 2020-02-07 09:52
 */
public class GVideoWebView extends WebView {
  //<editor-fold desc="构造函数">
  public GVideoWebView(@NonNull Context context) {
    super(context);
  }

  public GVideoWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public GVideoWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public GVideoWebView(
      @NonNull Context context,
      @Nullable AttributeSet attrs,
      int defStyleAttr,
      boolean privateBrowsing) {
    super(context, attrs, defStyleAttr, privateBrowsing);
  }
  //</editor-fold>
}
