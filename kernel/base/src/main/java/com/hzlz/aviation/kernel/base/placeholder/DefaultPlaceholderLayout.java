package com.hzlz.aviation.kernel.base.placeholder;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.kernel.base.BR;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultBinding;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultEmptyBinding;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultErrorBinding;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultLoadingBinding;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultNetworkNotAvailableBinding;
import com.hzlz.aviation.kernel.base.databinding.PlaceholderDefaultUnLoginBinding;

/**
 * 默认占位布局
 *
 *
 * @since 2020-01-07 00:01
 */
public final class DefaultPlaceholderLayout extends AbstractPlaceholderLayout {
  //<editor-fold desc="属性">
  @Nullable
  private PlaceholderDefaultBinding mBinding;
  @Nullable
  private ObjectAnimator mLoadingAnimator;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DefaultPlaceholderLayout() {

  }

  public DefaultPlaceholderLayout(boolean isDarkMode) {
    mIsDarkMode = isDarkMode;
  }

  public DefaultPlaceholderLayout(@Nullable PlaceholderDefaultBinding binding) {
    mBinding = binding;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@NonNull Context context) {
    if (mBinding == null) {
      mBinding = DataBindingUtil.inflate(
          LayoutInflater.from(context),
          R.layout.placeholder_default,
          null,
          false
      );
    }
    return mBinding.getRoot();
  }

  @Override
  public void updateType(int type, boolean isDarkMode, int paddingTop, boolean needShowTop) {
    if (needShowTop && mBinding != null) {
      mBinding.defaultContainer.setGravity(Gravity.TOP);
      mBinding.defaultContainer.setPadding(0, paddingTop, 0, 0);
    }
    super.updateType(type, isDarkMode, paddingTop, needShowTop);
  }

  @Nullable
  @Override
  protected View getRootView() {
    return mBinding == null ? null : mBinding.getRoot();
  }

  @Nullable
  @Override
  protected ViewStub getLoadingViewStub() {
    return mBinding == null ? null : mBinding.viewStubLoading.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getNetworkNotAvailableViewStub() {
    return mBinding == null ? null : mBinding.viewStubNetworkNotAvailable.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getErrorViewStub() {
    return mBinding == null ? null : mBinding.viewStubError.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getEmptyViewStub() {
    return mBinding == null ? null : mBinding.viewStubEmpty.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getEmptySearchViewStub() {
    return mBinding == null ? null : mBinding.viewStubEmptySearch.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getEmptyCommentViewStub() {
    return mBinding == null ? null : mBinding.viewStubEmptyComment.getViewStub();
  }

  @Nullable
  @Override
  protected ViewStub getUnLoginViewStub() {
    return mBinding == null ? null : mBinding.viewStubUnLogin.getViewStub();
  }

  @Override
  protected void onLoadingViewStubInflated(@NonNull View inflated) {
    super.onLoadingViewStubInflated(inflated);
    PlaceholderDefaultLoadingBinding binding = DataBindingUtil.bind(inflated);
    if (binding != null) {
      if (mLoadingAnimator != null) {
        mLoadingAnimator.cancel();
      }
      mLoadingAnimator = ObjectAnimator.ofFloat(binding.imageViewLoading, "rotation", 0, 360);
      mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
      mLoadingAnimator.setRepeatMode(ValueAnimator.RESTART);
      mLoadingAnimator.setDuration(1200L);
      mLoadingAnimator.start();
    }
  }

  @Override
  protected void onNetworkNotAvailableViewStubInflated(@NonNull View inflated) {
    super.onNetworkNotAvailableViewStubInflated(inflated);
    PlaceholderDefaultNetworkNotAvailableBinding binding = DataBindingUtil.bind(inflated);
    bindPlaceholderLayout(binding);
    if (binding != null) {
      binding.networkNotAvailableImage.setImageResource(
          mIsDarkMode ? R.drawable.ic_network_not_available_dark : R.drawable.ic_network_not_available);
    }
  }

  @Override
  protected void onErrorViewStubInflated(@NonNull View inflated) {
    super.onErrorViewStubInflated(inflated);
    PlaceholderDefaultErrorBinding binding = DataBindingUtil.bind(inflated);
    bindPlaceholderLayout(binding);
    if (binding != null) {
      binding.errorImage.setImageResource(
          mIsDarkMode ? R.drawable.ic_network_not_available_dark : R.drawable.ic_network_not_available);
    }
  }

  @Override
  protected void onUnLoginViewStub(@NonNull View inflated) {
    super.onUnLoginViewStub(inflated);
    PlaceholderDefaultUnLoginBinding binding = DataBindingUtil.bind(inflated);
    bindPlaceholderLayout(binding);
    if (binding != null) {
      binding.unLoginImg.setImageResource(
          mIsDarkMode ? R.drawable.ic_placeholder_empty_dark : R.drawable.ic_placeholder_empty);
    }
  }

  @Override
  protected void onEmptyViewStub(@NonNull View inflated) {
    super.onErrorViewStubInflated(inflated);
    PlaceholderDefaultEmptyBinding binding = DataBindingUtil.bind(inflated);
    if (binding != null) {
      binding.emptyImg.setImageResource(
          mIsDarkMode ? R.drawable.ic_placeholder_empty_dark : R.drawable.ic_placeholder_empty);
    }
  }

  @Override
  public void onDestroy() {
    if (mLoadingAnimator != null) {
      mLoadingAnimator.cancel();
    }
    super.onDestroy();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 绑定占位布局
   *
   * @param binding 待绑定的binding
   */
  private void bindPlaceholderLayout(ViewDataBinding binding) {
    if (binding != null) {
      binding.setVariable(BR.layout, this);
    }
  }
  //</editor-fold>
}