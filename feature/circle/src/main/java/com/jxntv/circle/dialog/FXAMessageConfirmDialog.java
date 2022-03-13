package com.jxntv.circle.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.DialogFxaMessageConfirmBinding;
import com.jxntv.circle.model.FXAModel;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.image.ImageLoaderManager;

/**
 * 放心爱个人信息确认
 */
public class FXAMessageConfirmDialog extends GVideoCenterDialog {


    private DialogFxaMessageConfirmBinding mLayoutBinding;

    public FXAMessageConfirmDialog(Context context) {
        super(context);

        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_fxa_message_confirm, null, false);
        setContentView(mLayoutBinding.getRoot());
        setCanceledOnTouchOutside(false);

    }

    public void setData(FXAModel model, View.OnClickListener listener){
        mLayoutBinding.setModel(model);
        ImageLoaderManager.loadImage(mLayoutBinding.avatar, UserManager.getCurrentUser().getAvatarUrl(),true);
        mLayoutBinding.confirm.setOnClickListener(view -> {
            dismiss();
            listener.onClick(view);
        });

    }

    @Override
    protected int getExpectWindowWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }
}
