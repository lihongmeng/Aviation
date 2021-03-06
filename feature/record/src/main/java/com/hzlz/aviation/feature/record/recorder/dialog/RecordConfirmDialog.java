package com.hzlz.aviation.feature.record.recorder.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.record.databinding.LayoutLongVideoLimitDialogBinding;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.feature.record.R;

/**
 * 录制确认弹窗
 */
public class RecordConfirmDialog extends GVideoCenterDialog {

  /** 持有的dataBind */
  private LayoutLongVideoLimitDialogBinding mLayoutBinding;

  /**
   * 构造函数
   */
  public RecordConfirmDialog(Context context) {
    super(context);
    mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
        R.layout.layout_long_video_limit_dialog, null,false);
    setContentView(mLayoutBinding.getRoot(),
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    );

    mLayoutBinding.confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    setCancelable(false);
  }
}
