package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.DialogItemViewSimpleCancelBinding;
import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;

/**
 * 简单的取消视图
 *
 *
 * @since 2020-02-05 21:26
 */
public final class SimpleCancelDialogItemView extends AbstractDialogItemView {
  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    DialogItemViewSimpleCancelBinding binding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_item_view_simple_cancel,
        parent,
        false
    );
    binding.setView(this);
    return binding.getRoot();
  }

  @Nullable
  @Override
  public Object getCurrentValue() {
    return null;
  }
  //</editor-fold>
}
