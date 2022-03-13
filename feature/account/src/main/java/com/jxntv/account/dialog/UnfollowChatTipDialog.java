package com.jxntv.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.account.R;
import com.jxntv.account.databinding.DialogUnfollowChatBinding;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.utils.ScreenUtils;

/**
 *  未关注聊天提示弹窗
 */
public class UnfollowChatTipDialog extends GVideoCenterDialog {

    /**
     * 持有的dataBind
     */
    private DialogUnfollowChatBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public UnfollowChatTipDialog(@NonNull Context context, View.OnClickListener clickListener) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_unfollow_chat, null, false);
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

        mLayoutBinding.cancel.setOnClickListener(view -> dismiss());
    }

    protected int getExpectWindowWidth() {
        return (int) (ScreenUtils.getScreenWidth(getContext()) * 0.75);
    }

}
