package com.hzlz.aviation.feature.home.splash.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.launch.LaunchProtocolHelper;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;

/**
 * 搜索删除确定弹窗
 */
public class SplashConfirmDialog extends GVideoCenterDialog {

    /** 持有的dataBind */
    private LayoutSplashConfirmDialogBinding mLayoutBinding;

    private boolean hasShowAgain = false;

    /**
     * 构造方法
     */
    public SplashConfirmDialog(@NonNull Context context, LaunchProtocolHelper.OnProtocolResultListener listener) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_splash_confirm_dialog, null,false);
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
                if (hasShowAgain) {
                    cancel();
                    listener.onCancel();
                    return;
                }
                hasShowAgain = true;
                showConfirm(false);
            }
        });
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

    /**
     * 显示确定弹窗
     *
     * @param isFirstTime 是否首次显示
     */
    public void showConfirm(boolean isFirstTime) {
        String title = isFirstTime ? GVideoRuntime.getAppContext().getResources().getString(R.string.splash_confirm) :
                GVideoRuntime.getAppContext().getResources().getString(R.string.splash_confirm_twice);
        if (isFirstTime) {
            hasShowAgain = false;
        }
        mLayoutBinding.splashConfirmTitle.setText(title);
        show();
    }

}
