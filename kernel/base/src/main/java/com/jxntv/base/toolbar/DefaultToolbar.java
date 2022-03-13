package com.jxntv.base.toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.jxntv.base.R;
import com.jxntv.base.databinding.ToolbarDefaultBinding;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.utils.ResourcesUtils;

/**
 * 默认 Toolbar
 *
 *
 * @since 2020-02-04 10:28
 */
public final class DefaultToolbar extends AbstractToolbar {
  //<editor-fold desc="属性">
  private ToolbarDefaultBinding mBinding;
  private int mToolbarHeight;
  //</editor-fold>

  private boolean needSetPadding;

  public DefaultToolbar() {
  }

  public DefaultToolbar(boolean needSetPadding) {
    this.needSetPadding = needSetPadding;
  }

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  protected View getToolbarView(@NonNull ViewGroup parent) {
    if (mBinding == null) {
      mBinding = DataBindingUtil.inflate(
          LayoutInflater.from(parent.getContext()),
          R.layout.toolbar_default,
          parent,
          false
      );
    }
    if (needSetPadding){
      mBinding.rootLayout.setPadding(0, WidgetUtils.getStatusBarHeight(),0,0);
    }else {
      mBinding.rootLayout.setPadding(0, 0,0,0);
    }
    return mBinding.getRoot();
  }

  @Nullable
  @Override
  protected TextView getTitleTextView() {
    return mBinding.textViewTitle;
  }

  @Nullable
  @Override
  protected ImageView getLeftBackImageView() {
    return mBinding.imageViewLeftBack;
  }

  @Nullable
  @Override
  protected ImageView getRightImageView() {
    return mBinding.imageViewRightOperation;
  }

  @Nullable
  @Override
  protected TextView getLeftBackTextView() {
    return null;
  }

  @Nullable
  @Override
  protected TextView getRightOperationTextView() {
    return mBinding.textViewRightOperation;
  }

  @Override
  public int getHeight() {
    int statusBarHeight = WidgetUtils.getStatusBarHeight();
    if (mToolbarHeight == 0) {
      mToolbarHeight = (int) ResourcesUtils.getDimens(R.dimen.height_toolbar);
      if (needSetPadding) {
        mToolbarHeight += statusBarHeight;
      }
    }
    return mToolbarHeight;
  }

  @Override public void hide() {
    mBinding.getRoot().setVisibility(View.GONE);
  }

  @Override
  public String getToolbarTitle() {
    return mBinding != null ? mBinding.textViewTitle.getText().toString() : null;
  }

  //</editor-fold>
}
