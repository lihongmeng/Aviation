package com.hzlz.aviation.feature.home.update;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.databinding.LayoutErrorDialogBinding;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;

/**
 * 更新错误弹窗
 */
public class UpdateErrorDialog extends GVideoCenterDialog {

    private static final int MAX_PROGRESS = 100;
    /**
     * 持有的dataBind
     */
    private LayoutErrorDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public UpdateErrorDialog(@NonNull Context context, View.OnClickListener clickListener) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_error_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        setCanceledOnTouchOutside(false);
        mLayoutBinding.confirm.setOnClickListener(view -> {
            clickListener.onClick(view);
            dismiss();
        });
    }

}
