package com.hzlz.aviation.feature.record.recorder.dialog;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.feature.record.R;

/**
 * 处理video 弹窗
 */
public class ProcessVideoDialog extends GVideoCenterDialog {

    /**
     * 持有的dataBind
     */
    private LayoutProcessDialogBinding mLayoutBinding;
    /**
     * loading动画
     */
    private ObjectAnimator mLoadingAnimator;

    /**
     * 构造方法
     */
    public ProcessVideoDialog(@NonNull Context context) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_process_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ResourcesUtils.getIntDimens(R.dimen.loading_dialog_width),
                        ResourcesUtils.getIntDimens(R.dimen.loading_dialog_height)
                )
        );
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        if (mLoadingAnimator != null) {
            mLoadingAnimator.cancel();
        }
        mLoadingAnimator = ObjectAnimator.ofFloat(mLayoutBinding.imageViewLoading,
                "rotation", 0, 360);
        mLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnimator.setRepeatMode(ValueAnimator.RESTART);
        mLoadingAnimator.setDuration(1200L);
        mLoadingAnimator.start();
    }

    public void setTipText(String tipText) {
        mLayoutBinding.textView.setText(tipText);
    }
}
