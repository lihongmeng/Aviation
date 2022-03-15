package com.hzlz.aviation.kernel.base.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.DefaultNetworkDialogBinding;

/**
 * 默认网络弹窗
 *
 *
 * since 2020-01-07 01:17
 */
public final class DefaultNetworkDialog extends AlertDialog implements INetworkDialog {
  //<editor-fold desc="属性">
  private DefaultNetworkDialogBinding mBinding;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DefaultNetworkDialog(@NonNull Context context) {
    super(context);
    mBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.default_network_dialog, null, false
    );
    // 设置不可取消
    setCancelable(false);
    // 设置空白的背景
    Window window = getWindow();
    if (window != null) {
      window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    setView(mBinding.getRoot());
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void onDestroy() {
    if (isShowing()) {
      dismiss();
    }
  }

  @Override
  public void show(@StringRes int textResId) {
    try {
      show(getContext().getString(textResId));
    } catch (Resources.NotFoundException e) {
      show(null);
    }
  }

  @Override
  public void show(@Nullable String text) {
    if (text == null) {
      mBinding.textViewTip.setText(R.string.default_network_dialog_loading);
    } else {
      mBinding.textViewTip.setText(text);
    }
    show();
  }

  @Override
  public void hideDialog() {
    dismiss();
  }
  //</editor-fold>
}
