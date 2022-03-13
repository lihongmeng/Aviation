package com.jxntv.record.recorder.dialog;

import android.content.Context;
import android.view.View;
import com.jxntv.base.dialog.DefaultEnsureCancelDialog;
import com.jxntv.record.R;
import com.jxntv.utils.ResourcesUtils;

/**
 * 发布确认弹窗
 */
public class PublishConfirmCancelDialog extends DefaultEnsureCancelDialog {

  /**
   * 构造函数
   */
  public PublishConfirmCancelDialog(Context context) {
    super(context);
  }

  /**
   * 初始化
   *
   * @param cancelListener    取消处理监听
   * @param confirmListener   确认处理监听
   */
  public void init(View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
    super.init(cancelListener, confirmListener,
        ResourcesUtils.getString(R.string.publish_ensure_title),
        ResourcesUtils.getString(R.string.publish_ensure_text));
    mLayoutBinding.confirm.setText(R.string.publish_ensure);
    mLayoutBinding.cancel.setText(R.string.save_local);
  }
}
