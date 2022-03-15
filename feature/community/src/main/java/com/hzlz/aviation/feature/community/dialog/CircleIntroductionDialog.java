package com.hzlz.aviation.feature.community.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.DialogIntroductionMessageBinding;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;

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
