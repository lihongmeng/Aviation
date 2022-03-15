package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.DialogFollowCancelBinding;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;

/**
 *  取消关注
 */
public class FollowCancelDialog extends GVideoBottomSheetDialog {

    /**
     * 持有的dataBind
     */
    private DialogFollowCancelBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public FollowCancelDialog(@NonNull Context context, View.OnClickListener clickListener) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_follow_cancel, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        setCanceledOnTouchOutside(true);
        mLayoutBinding.confirm.setOnClickListener(view -> {
            clickListener.onClick(view);
            dismiss();
        });

        mLayoutBinding.cancel.setOnClickListener(view -> dismiss());
    }

}
