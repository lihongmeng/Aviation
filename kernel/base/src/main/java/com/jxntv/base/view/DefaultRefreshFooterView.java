package com.jxntv.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.R;
import com.jxntv.base.adapter.DefaultNoMoreDataViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 默认底部刷新视图
 *
 *
 * @since 2020.1.17
 */
public class DefaultRefreshFooterView extends FrameLayout implements RefreshFooter {

  private TextView mRootView;

  public DefaultRefreshFooterView(@NonNull Context context) {
    super(context);
    initView();
  }

  public DefaultRefreshFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  public DefaultRefreshFooterView(@NonNull Context context, @Nullable AttributeSet attrs,
                                  int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  /**
   * 初始化默认视图
   */
  private void initView() {
    View view = DefaultNoMoreDataViewHolder.createView(this, R.string.all_try_load_more);
    if (view instanceof TextView) {
      mRootView = (TextView) view;
    }
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
    updateTextViewText(R.string.all_try_load_more);
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

  public void updateTextViewText(int stringRes) {
    if (mRootView != null) {
      mRootView.setText(stringRes);
    }
  }
}
