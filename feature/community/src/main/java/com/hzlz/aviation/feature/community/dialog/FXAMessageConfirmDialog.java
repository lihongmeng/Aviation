package com.hzlz.aviation.feature.community.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.DialogFxaMessageConfirmBinding;
import com.hzlz.aviation.feature.community.model.FXAModel;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

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
