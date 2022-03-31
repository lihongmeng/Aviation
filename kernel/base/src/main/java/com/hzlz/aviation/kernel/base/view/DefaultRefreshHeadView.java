package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

/**
 * 默认刷新视图
 */
public class DefaultRefreshHeadView extends FrameLayout implements RefreshHeader {

  /** 持有的动画视图 */
  private AviationImageView mAnimView;
  /** 进度视图 */
  private RoundProgressView mProgressView;
  /** 动画drawable */
  private AnimationDrawable mAnimDrawable;

  /**
   * 构造方法
   */
  public DefaultRefreshHeadView(@NonNull Context context) {
    super(context);
    initView();
  }

  /**
   * 构造方法
   */
  public DefaultRefreshHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView();
  }

  /**
   * 构造方法
   */
  public DefaultRefreshHeadView(@NonNull Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  /**
   * 初始化view
   */
  private void initView() {
    int size = (int) (28 * getResources().getDisplayMetrics().density + 0.5f);
    mProgressView = new RoundProgressView(getContext());
    LayoutParams layoutParams = new LayoutParams(size, size);
    layoutParams.gravity = Gravity.CENTER;
    addView(mProgressView, layoutParams);
    mAnimView = new AviationImageView(getContext());
    mAnimView.setImageResource(R.drawable.universal_refresh_anim);
    mAnimDrawable = (AnimationDrawable) mAnimView.getDrawable();
    size = (int) (30 * getResources().getDisplayMetrics().density + 0.5f);
    layoutParams = new LayoutParams(size, size);
    layoutParams.gravity = Gravity.CENTER;
    addView(mAnimView, layoutParams);
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
  public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
    if (isDragging && percent > 0.6f) {
      mProgressView.setProgress((int) (100 * (percent * 2.5f - 1.5f)));
    }
  }


  @Override
  public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    mProgressView.setProgress(0);
    mProgressView.setVisibility(INVISIBLE);
    mAnimView.setVisibility(VISIBLE);
    mAnimDrawable.start();
  }

  @Override
  public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

  }

  @Override
  public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
    mAnimDrawable.stop();
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
    if (oldState == RefreshState.None && newState == RefreshState.PullDownToRefresh) {
      mProgressView.setProgress(0);
      mProgressView.setVisibility(VISIBLE);
      mAnimView.setVisibility(INVISIBLE);
    }
  }
}
