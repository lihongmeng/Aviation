package com.hzlz.aviation.kernel.base.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.hzlz.aviation.kernel.base.R;

/**
 * 可见透明度 Behavior
 *
 *
 * @since 2020-02-17 20:50
 */
public final class VisibilityAlphaBehavior extends CoordinatorLayout.Behavior<View> {
  //<editor-fold desc="属性">
  private int mReferenceId;
  private boolean mVisibleWhenScrollUp;
  private float mVisibleMinScrollDistance;
  private float mScrollFactor;
  private float mVisibleSubHeight;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public VisibilityAlphaBehavior() {
  }

  public VisibilityAlphaBehavior(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VisibilityAlphaBehavior);
    mReferenceId = ta.getResourceId(
        R.styleable.VisibilityAlphaBehavior_vabReferenceViewId, View.NO_ID
    );
    mVisibleWhenScrollUp = ta.getBoolean(
        R.styleable.VisibilityAlphaBehavior_vabVisibleWhenScrollUp, true
    );
    mVisibleMinScrollDistance = ta.getDimension(
        R.styleable.VisibilityAlphaBehavior_vabVisibleMinScrollDistance, 0f
    );
    mScrollFactor = ta.getFloat(R.styleable.VisibilityAlphaBehavior_vabScrollFactor, 1f);
    if (mScrollFactor < 1f) {
      mScrollFactor = 1f;
    }
    mVisibleSubHeight = ta.getDimension(
        R.styleable.VisibilityAlphaBehavior_vabVisibleSubHeight, 0f
    );
    ta.recycle();
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  public boolean onMeasureChild(
      @NonNull CoordinatorLayout parent,
      @NonNull View child,
      int parentWidthMeasureSpec,
      int widthUsed,
      int parentHeightMeasureSpec,
      int heightUsed) {
    return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed,
        parentHeightMeasureSpec, heightUsed);
  }

  @Override
  public boolean layoutDependsOn(
      @NonNull CoordinatorLayout parent,
      @NonNull View child,
      @NonNull View dependency) {
    return dependency.getId() == mReferenceId;
  }

  @Override
  public boolean onDependentViewChanged(
      @NonNull CoordinatorLayout parent,
      @NonNull View child,
      @NonNull View dependency) {
    // 计算 alpha 值
    float scrollDistance =
        Math.abs(dependency.getTop() * mScrollFactor) - mVisibleMinScrollDistance;
    if (scrollDistance < 0) {
      scrollDistance = 0;
    }
    float totalDistance = dependency.getHeight() - child.getHeight() - mVisibleSubHeight - mVisibleMinScrollDistance;
    if (!mVisibleWhenScrollUp) {
      scrollDistance = Math.abs(totalDistance - scrollDistance);
    }
    float alpha = scrollDistance / totalDistance;
    // 设置可见性
    int visibility = alpha > 0 ? View.VISIBLE : View.GONE;
    int oldVisibility = child.getVisibility();
    if (visibility != oldVisibility) {
      child.setVisibility(visibility);
    }
    // 设置 alpha 值
    if (visibility == View.VISIBLE && alpha >= 0) {
      float oldAlpha = child.getAlpha();
      if (alpha != oldAlpha) {
        child.setAlpha(alpha);
      }
    }
    return super.onDependentViewChanged(parent, child, dependency);
  }

  //</editor-fold>
}
