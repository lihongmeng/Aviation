package com.hzlz.aviation.feature.record.recorder.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.record.databinding.LayoutPublishProgressDialogBinding;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.feature.record.R;

/**
 * 发布进度弹窗
 */
public class PublishProgressVideoDialog extends GVideoCenterDialog {

  /** 持有的dataBind */
  private LayoutPublishProgressDialogBinding mLayoutBinding;
  /** 进度格式 */
  private String mProgressFormat;

  /**
   * 构造函数
   */
  public PublishProgressVideoDialog(Context context) {
    super(context);
    mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.layout_publish_progress_dialog, null,false);
    setContentView(mLayoutBinding.getRoot(),
        new ViewGroup.LayoutParams(ResourcesUtils.getIntDimens(R.dimen.publish_dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT)
    );
    mProgressFormat = context.getResources().getString(R.string.publish_progress_text);
    setCanceledOnTouchOutside(false);
    setCancelable(false);
  }

  /**
   * 更新进度比例
   *
   * @param rate  更新的进度比例
   */
  public void updateProgressText(int rate) {
    mLayoutBinding.textProgress.setText(String.format(mProgressFormat, rate));
  }
}
