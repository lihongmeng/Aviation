package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;

/**
 * 分割线弹窗 Item 视图
 *
 *
 * @since 2020-02-19 18:45
 */
public final class DriverDialogItemView extends AbstractDialogItemView {
  //<editor-fold desc="属性">
  private int mDriverColor;
  private int mDriverHeight;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DriverDialogItemView() {
    this(Color.parseColor("#EBECF0"), 1);
  }

  public DriverDialogItemView(@ColorInt int driverColor, int driverHeight) {
    mDriverColor = driverColor;
    mDriverHeight = driverHeight;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    View view = new View(context);
    view.setBackgroundColor(mDriverColor);
    view.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, mDriverHeight
    ));
    return view;
  }

  @Nullable
  @Override
  public Object getCurrentValue() {
    return null;
  }
  //</editor-fold>
}
