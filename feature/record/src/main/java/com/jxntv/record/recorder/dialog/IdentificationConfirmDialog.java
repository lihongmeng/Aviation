package com.jxntv.record.recorder.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.record.R;
import com.jxntv.record.databinding.LayoutIdentifacationConfirmDialogBinding;

/**
 * 认证弹窗
 */
public class IdentificationConfirmDialog extends GVideoCenterDialog {

  /** 持有的dataBind */
  protected LayoutIdentifacationConfirmDialogBinding mLayoutBinding;

  /**
   * 构造方法
   */
  public IdentificationConfirmDialog(@NonNull Context context) {
    super(context);
    mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.layout_identifacation_confirm_dialog, null,false);
    setContentView(mLayoutBinding.getRoot(),
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    );

  }

  public void setConfirmDialog(@StringRes int res){
    mLayoutBinding.splashConfirmTitle.setText(res);
  }

  /**
   * 初始化
   *
   * @param cancelListener    取消按钮点击监听
   * @param confirmListener   确定按钮点击监听
   */
  public void init(View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
    if (cancelListener != null) {
      mLayoutBinding.splashDialogCancel.setOnClickListener(cancelListener);
    }
    if (confirmListener != null) {
      mLayoutBinding.splashDialogConfirm.setOnClickListener(confirmListener);
    }
  }
}
