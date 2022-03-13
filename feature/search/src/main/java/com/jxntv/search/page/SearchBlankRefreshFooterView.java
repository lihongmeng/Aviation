package com.jxntv.search.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 搜索页面空白占位footer view
 */
public class SearchBlankRefreshFooterView extends FrameLayout implements RefreshFooter {

  public SearchBlankRefreshFooterView(@NonNull Context context) {
    super(context);
    initView(context);
  }

  public SearchBlankRefreshFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public SearchBlankRefreshFooterView(@NonNull Context context, @Nullable AttributeSet attrs,
                                      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context);
  }

  /**
   * 初始化默认视图
   */
  private void initView(Context context) {
    View view = new View(context, null);
    view.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    ));
    addView(view);
  }

  @Override
  public boolean setNoMoreData(boolean noMoreData) {
    return true;
  }

  @NonNull
  @Override
  public View getView() {
    return this;
  }

  @NonNull
  @Override
  public SpinnerStyle getSpinnerStyle() {
    return SpinnerStyle.Translate;
  }

  @Override
  public void setPrimaryColors(int... colors) {

  }

  @Override
  public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

  }

  @Override
  public void onMoving(boolean isDragging, float percent, int offset, int height,
      int maxDragHeight) {

  }

  @Override
  public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
  }

  @Override
  public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

  }

  @Override
  public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
    return 0;
  }

  @Override
  public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

  }

  @Override
  public boolean isSupportHorizontalDrag() {
    return false;
  }

  @Override
  public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState,
                             @NonNull RefreshState newState) {
  }
}
