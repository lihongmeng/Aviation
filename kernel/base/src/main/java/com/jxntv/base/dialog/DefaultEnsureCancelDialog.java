package com.jxntv.base.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.R;
import com.jxntv.base.databinding.LayoutEnsureCancelDialogBinding;
import com.jxntv.dialog.GVideoCenterDialog;

/**
 * 搜索删除确定弹窗
 */
public class DefaultEnsureCancelDialog extends GVideoCenterDialog {

    /**
     * 持有的dataBind
     */
    protected LayoutEnsureCancelDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public DefaultEnsureCancelDialog(@NonNull Context context) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_ensure_cancel_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

    }

    /**
     * 初始化
     *
     * @param cancelListener  取消按钮点击监听
     * @param confirmListener 确定按钮点击监听
     * @param title           dialog标题
     * @param text            dialog文字
     */
    public void init(View.OnClickListener cancelListener, View.OnClickListener confirmListener,
                     String title, String text) {
        if (cancelListener != null) {
            mLayoutBinding.cancel.setOnClickListener(cancelListener);
        }
        if (confirmListener != null) {
            mLayoutBinding.confirm.setOnClickListener(confirmListener);
        }
        if (!TextUtils.isEmpty(title)) {
            mLayoutBinding.title.setVisibility(View.VISIBLE);
            mLayoutBinding.title.setText(title);
        } else {
            mLayoutBinding.title.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(text)) {
            mLayoutBinding.content.setText(text);
        }
    }

    /**
     * 初始化
     *
     * @param cancelListener  取消按钮点击监听
     * @param confirmListener 确定按钮点击监听
     * @param title           dialog标题
     * @param text            dialog文字
     * @param leftText        左侧按钮文字
     * @param rightText       右侧按钮文字
     */
    public void init(View.OnClickListener cancelListener,
                     View.OnClickListener confirmListener,
                     String title,
                     String text,
                     String leftText,
                     String rightText
    ) {
        if (cancelListener != null) {
            mLayoutBinding.cancel.setOnClickListener(cancelListener);
        }
        if (confirmListener != null) {
            mLayoutBinding.confirm.setOnClickListener(confirmListener);
        }
        if (!TextUtils.isEmpty(title)) {
            mLayoutBinding.title.setVisibility(View.VISIBLE);
            mLayoutBinding.title.setText(title);
        } else {
            mLayoutBinding.title.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(text)) {
            mLayoutBinding.content.setText(text);
        }
        mLayoutBinding.cancel.setText(leftText);
        mLayoutBinding.confirm.setText(rightText);


    }
}
