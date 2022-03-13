package com.jxntv.base.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.jxntv.base.R;
import com.jxntv.base.databinding.DialogAdminCanNotExitBinding;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.ScreenUtils;

/**
 * 圈子简介弹窗
 */
public class DefaultEnsureDialog extends GVideoCenterDialog {

    private DialogAdminCanNotExitBinding mLayoutBinding;

    public DefaultEnsureDialog(Context context) {
        super(context);

        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_admin_can_not_exit, null, false);
        setContentView(mLayoutBinding.getRoot());
        setCanceledOnTouchOutside(false);

    }

    public void setData(String content) {
        mLayoutBinding.content.setText(content);
        mLayoutBinding.confirm.setOnClickListener(view -> dismiss());
    }

    @Override
    protected int getExpectWindowWidth() {
        return (int) (ScreenUtils.getScreenWidth(getContext()) * 0.6);
    }


    /**
     * 初始化
     *
     * @param confirmListener 确定按钮点击监听
     * @param text            dialog文字
     */
    public void init(View.OnClickListener confirmListener, String text, String confirmContent) {
        if (confirmListener != null) {
            mLayoutBinding.confirm.setOnClickListener(confirmListener);
        }
        if (!TextUtils.isEmpty(text)) {
            mLayoutBinding.content.setText(text);
        }
        if (!TextUtils.isEmpty(confirmContent)) {
            mLayoutBinding.confirm.setText(confirmContent);
        }
    }

    public void showJoinCommunityStyle() {
        setContentTextSize(R.dimen.sp_13);
        setContentTextColor(R.color.color_7f7f7f);
        setButtonTextSize(R.dimen.sp_17);
        setButtonTextColor(R.color.color_333333);
        mLayoutBinding.confirm.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    public void setButtonTextColor(int resourceId) {
        mLayoutBinding.confirm.setTextColor(ResourcesUtils.getColor(resourceId));
    }

    public void setButtonTextSize(int resourceId) {
        mLayoutBinding.confirm.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimensionPixelSize(resourceId)
        );
    }

    public void setContentTextColor(int resourceId) {
        mLayoutBinding.content.setTextColor(ResourcesUtils.getColor(resourceId));
    }

    public void setContentTextSize(int resourceId) {
        mLayoutBinding.content.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getContext().getResources().getDimensionPixelSize(resourceId)
        );
    }


}
