package com.jxntv.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.account.R;
import com.jxntv.account.databinding.DialogFollowCancelBinding;
import com.jxntv.account.databinding.DialogUnfollowChatBinding;
import com.jxntv.dialog.GVideoBottomSheetDialog;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.utils.ScreenUtils;

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
