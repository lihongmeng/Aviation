package com.jxntv.base.toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.jxntv.widget.GVideoLinearLayout;

/**
 * Toolbar 工具类
 *
 *
 * @since 2020-02-04 11:42
 */
public final class ToolbarUtils {
  //<editor-fold desc="私有构造函数">
  private ToolbarUtils() {
    throw new IllegalStateException("no instance!!");
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 添加 Toolbar
   *
   * @param parent 父视图
   * @param toolbar Toolbar
   */
  public static void addToolbar(@NonNull View parent, @NonNull IToolbar toolbar) {
    if (!(parent instanceof ViewGroup)) {
      return;
    }
    // 判断是否为滚动视图
    if (parent instanceof NestedScrollView || parent instanceof ScrollView) {
      View firstChild = ((FrameLayout) parent).getChildAt(0);
      if (firstChild != null) {
        addToolbar(firstChild, toolbar);
      }
      return;
    }
    // 准备 toolbar 信息
    int height = toolbar.getHeight();
    View toolbarView = toolbar.getView((ViewGroup) parent);
    toolbarView.setId(ViewCompat.generateViewId());
    // 判断 parent 类型并添加视图
    if (parent instanceof LinearLayout) {
      // 添加视图
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, height
      );
      ((LinearLayout) parent).addView(toolbarView, 0, params);
    } else if (parent instanceof RelativeLayout) {
      // 添加视图
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, height
      );
      ((RelativeLayout) parent).addView(toolbarView, 0, params);
      // 调整第二个子 View
      View secondView = ((RelativeLayout) parent).getChildAt(1);
      if (secondView != null) {
        RelativeLayout.LayoutParams secondParams =
            (RelativeLayout.LayoutParams) secondView.getLayoutParams();
        secondParams.addRule(RelativeLayout.BELOW, toolbarView.getId());
        secondView.setLayoutParams(secondParams);
      }
    } else if (parent instanceof FrameLayout) {
      // 添加视图
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, height
      );
      ((FrameLayout) parent).addView(toolbarView, 0, params);
      // 调整第二个子 View
      View secondView = ((FrameLayout) parent).getChildAt(1);
      if (secondView != null) {
        FrameLayout.LayoutParams secondParams =
            (FrameLayout.LayoutParams) secondView.getLayoutParams();
        secondParams.topMargin += height;
        secondView.setLayoutParams(secondParams);
      }
    } else if (parent instanceof ConstraintLayout) {
      // 添加视图
      ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, height);
      params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
      params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
      params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
      ((ConstraintLayout) parent).addView(toolbarView, 0, params);
      // 调整第二个子 View
      View secondView = ((ConstraintLayout) parent).getChildAt(1);
      if (secondView != null) {
        ConstraintLayout.LayoutParams secondParams =
            (ConstraintLayout.LayoutParams) secondView.getLayoutParams();
        secondParams.topToBottom = toolbarView.getId();
        secondParams.topToTop = ConstraintLayout.LayoutParams.UNSET;
        secondView.setLayoutParams(secondParams);
      }
    } else {
      throw new IllegalStateException("un support parent");
    }
  }
  //</editor-fold>
}
