package com.hzlz.aviation.kernel.base.model;

import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * 解决 ViewModel 中无法调用 StartActivityForResult 方法
 *
 *
 * @since 2020-02-03 18:18
 */
public final class StartActivityForResultModel {
  //<editor-fold desc="属性">
  @NonNull
  private Intent mIntent;
  private int mRequestCode;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  public StartActivityForResultModel(@NonNull Intent intent, int requestCode) {
    mIntent = intent;
    mRequestCode = requestCode;
  }

  //</editor-fold>

  //<editor-fold desc="Getter">
  @NonNull
  public Intent getIntent() {
    return mIntent;
  }

  public int getRequestCode() {
    return mRequestCode;
  }
  //</editor-fold>
}
