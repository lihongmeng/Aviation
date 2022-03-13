package com.jxntv.circle.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.DialogIntroductionMessageBinding;
import com.jxntv.circle.model.FXAModel;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.utils.ScreenUtils;

/**
 * 圈子简介弹窗
 */
public class CircleIntroductionDialog extends GVideoCenterDialog {


    private DialogIntroductionMessageBinding mLayoutBinding;

    public CircleIntroductionDialog(Context context) {
        super(context);

        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_introduction_message, null, false);
        setContentView(mLayoutBinding.getRoot());
        setCanceledOnTouchOutside(true);

    }

    public void setData(String content){
        mLayoutBinding.content.setText(content);
        mLayoutBinding.btn.setOnClickListener(view -> dismiss());
    }

    @Override
    protected int getExpectWindowWidth() {
        return (int) (ScreenUtils.getScreenWidth(getContext()) * 0.85);
    }

}
