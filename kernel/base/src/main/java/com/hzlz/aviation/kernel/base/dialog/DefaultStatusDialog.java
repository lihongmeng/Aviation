package com.hzlz.aviation.kernel.base.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.LayoutStatusDialogBinding;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;

/**
 * 默认状态弹窗
 */
public class DefaultStatusDialog extends GVideoCenterDialog {

    /** type:成功 */
    public static final int TYPE_SUCCESS = 0;
    /** type:失败 */
    public static final int TYPE_FAIL = 1;

    /** 持有的dataBind */
    protected LayoutStatusDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public DefaultStatusDialog(@NonNull Context context,int theme, int type, String text) {
        super(context, theme);
        setCancelable(false);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_status_dialog, null,false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        if (type == TYPE_SUCCESS) {
            mLayoutBinding.statusImg.setImageResource(R.drawable.ic_success);
        } else {
            mLayoutBinding.statusImg.setImageResource(R.drawable.ic_fail);
        }
        mLayoutBinding.statusText.setText(text == null ? "" : text);
    }
}
