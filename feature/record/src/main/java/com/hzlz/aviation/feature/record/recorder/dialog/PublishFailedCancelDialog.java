package com.hzlz.aviation.feature.record.recorder.dialog;

import android.content.Context;
import android.view.View;

import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.record.R;

/**
 * 发布确认弹窗
 */
public class PublishFailedCancelDialog extends DefaultEnsureCancelDialog {

    /**
     * 构造函数
     */
    public PublishFailedCancelDialog(Context context) {
        super(context);
    }

    /**
     * 初始化
     *
     * @param cancelListener  取消处理监听0
     * @param confirmListener 确认处理监听
     */
    public void init(View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
        super.init(cancelListener, confirmListener,
                ResourcesUtils.getString(R.string.upload_failed),
                ResourcesUtils.getString(R.string.please_check_network_available));
        mLayoutBinding.confirm.setText(R.string.upload_again);
        mLayoutBinding.cancel.setText(R.string.cancel_upload);
    }

    public void init(View.OnClickListener confirmListener){
        init(view -> dismiss(), view -> {
            confirmListener.onClick(view);
            dismiss();
        });
    }
}
