package com.hzlz.aviation.kernel.base.placeholder;

import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY_COMMENT;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY_SEARCH;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.ERROR;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.LOADING;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NONE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.UN_LOGIN;

import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 占位布局抽象实现类
 *
 *
 * @since 2020-01-09 15:01
 */
public abstract class AbstractPlaceholderLayout implements IPlaceholderLayout,
    ViewStub.OnInflateListener {
  //<editor-fold desc="属性">
  @Nullable
  protected PlaceholderListener mListener;

  /** 是否为暗黑模式 */
  protected boolean mIsDarkMode;
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 获取根布局
   *
   * @return 根布局
   */
  @Nullable
  protected abstract View getRootView();

  /**
   * 获取加载中的 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getLoadingViewStub();

  /**
   * 获取网络不可用的 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getNetworkNotAvailableViewStub();

  /**
   * 获取错误的 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getErrorViewStub();

  /**
   * 获取空数据的 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getEmptyViewStub();


  /**
   * 获取空数据的搜素 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getEmptySearchViewStub();

  /**
   * 获取空数据的评论 ViewStub
   *
   * @return 加载中的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getEmptyCommentViewStub();


  /**
   * 获取未登录的 ViewStub
   *
   * @return 未登录的 ViewStub
   */
  @Nullable
  protected abstract ViewStub getUnLoginViewStub();
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void updateType(@PlaceholderType int type, boolean isDarkMode, int paddingTop, boolean needShowTop) {
    mIsDarkMode = isDarkMode;
    // loading
    ViewStub loadingViewStub = getLoadingViewStub();
    if (loadingViewStub != null) {
      loadingViewStub.setOnInflateListener(this);
      loadingViewStub.setVisibility(type == LOADING ? View.VISIBLE : View.GONE);
    }
    // network not available
    ViewStub networkNotAvailableViewStub = getNetworkNotAvailableViewStub();
    if (networkNotAvailableViewStub != null) {
      networkNotAvailableViewStub.setOnInflateListener(this);
      networkNotAvailableViewStub.setVisibility(
          type == NETWORK_NOT_AVAILABLE ? View.VISIBLE : View.GONE
      );
    }
    // error
    ViewStub errorViewStub = getErrorViewStub();
    if (errorViewStub != null) {
      errorViewStub.setOnInflateListener(this);
      errorViewStub.setVisibility(type == ERROR ? View.VISIBLE : View.GONE);
    }
    // empty
    ViewStub emptyViewStub = getEmptyViewStub();
    if (emptyViewStub != null) {
      emptyViewStub.setOnInflateListener(this);
      emptyViewStub.setVisibility(type == EMPTY ? View.VISIBLE : View.GONE);
    }
    // empty
    ViewStub emptySearchViewStub = getEmptySearchViewStub();
    if (emptySearchViewStub != null) {
      emptySearchViewStub.setOnInflateListener(this);
      emptySearchViewStub.setVisibility(type == EMPTY_SEARCH ? View.VISIBLE : View.GONE);
    }
    ViewStub emptyCommentViewStub = getEmptyCommentViewStub();
    if (emptyCommentViewStub != null) {
      emptyCommentViewStub.setOnInflateListener(this);
      emptyCommentViewStub.setVisibility(type == EMPTY_COMMENT ? View.VISIBLE : View.GONE);
    }
    // un_login
    ViewStub unLoginViewStub = getUnLoginViewStub();
    if (unLoginViewStub != null) {
      unLoginViewStub.setOnInflateListener(this);
      unLoginViewStub.setVisibility(type == UN_LOGIN ? View.VISIBLE : View.GONE);
    }
    // none
    View rootView = getRootView();
    if (rootView != null) {
      rootView.setVisibility(type == NONE ? View.GONE : View.VISIBLE);
    }
  }

  @Override
  public void setPlaceholderListener(@Nullable PlaceholderListener listener) {
    mListener = listener;
  }

  @Override
  public void onDestroy() {

  }

  @Override
  public void onInflate(@NonNull ViewStub stub, @NonNull View inflated) {
    if (stub.equals(getLoadingViewStub())) {
      onLoadingViewStubInflated(inflated);
    } else if (stub.equals(getNetworkNotAvailableViewStub())) {
      onNetworkNotAvailableViewStubInflated(inflated);
    } else if (stub.equals(getErrorViewStub())) {
      onErrorViewStubInflated(inflated);
    } else if (stub.equals(getEmptyViewStub())) {
      onEmptyViewStub(inflated);
    } else if (stub.equals(getUnLoginViewStub())) {
      onUnLoginViewStub(inflated);
    }else if (stub.equals(getEmptySearchViewStub())){
      onEmptySearchViewStub(inflated);
    }else if (stub.equals(getEmptyCommentViewStub())){
      onEmptyCommentViewStub(inflated);
    }
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 获取加载中的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onLoadingViewStubInflated(@NonNull View inflated) {

  }

  /**
   * 获取网络不可用的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onNetworkNotAvailableViewStubInflated(@NonNull View inflated) {

  }

  /**
   * 获取错误的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onErrorViewStubInflated(@NonNull View inflated) {

  }

  /**
   * 获取空数据的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onEmptyViewStub(@NonNull View inflated) {

  }

  /**
   * 获取空数据的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onEmptySearchViewStub(@NonNull View inflated) {

  }

  /**
   * 获取空数据的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onEmptyCommentViewStub(@NonNull View inflated) {

  }


  /**
   * 获取未登录的 ViewStub 被显示出来时进行回调，只会执行一次
   *
   * @param inflated View
   */
  protected void onUnLoginViewStub(@NonNull View inflated) {

  }
  //</editor-fold>

  //<editor-fold desc="公开方法">
  public void login(@NonNull View view) {
    if (mListener != null) {
      mListener.onLogin(view);
    }
  }

  /**
   * 重新加载
   */
  public void reload(@NonNull View view) {
    if (mListener != null) {
      mListener.onReload(view);
    }
  }

  /**
   * 是否为暗黑模式
   */
  public boolean isDarkMode() {
    return mIsDarkMode;
  }
  //</editor-fold>
}
