package com.jxntv.home.update;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.home.R;
import com.jxntv.home.databinding.LayoutSplashConfirmDialogBinding;

public class UpdateDialog extends GVideoCenterDialog {

  /** 持有的dataBind */
  private LayoutSplashConfirmDialogBinding mLayoutBinding;

  /**
   * 构造方法
   */
  public UpdateDialog(@NonNull Context context) {
    super(context);
    mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.layout_splash_confirm_dialog, null,false);
    mLayoutBinding.splashDialogConfirm.setText(R.string.update_bt_confirm);
    mLayoutBinding.splashDialogCancel.setText(R.string.update_bt_cancel);
    setContentView(mLayoutBinding.getRoot(),
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    );
    setCanceledOnTouchOutside(false);
    mLayoutBinding.splashDialogCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }

  public void setContent(String content) {
    mLayoutBinding.splashConfirmTitle.setText(content);
  }

  public void setConfirmText(@StringRes int  confirm){
    mLayoutBinding.splashDialogConfirm.setText(confirm);
  }

  /**
   * 设置确定监听器
   *
   * @param listener  取消监听器
   */
  public void setConfirmListener(View.OnClickListener listener) {
    if (listener == null) {
      return;
    }
    mLayoutBinding.splashDialogConfirm.setOnClickListener(listener);
  }

}
