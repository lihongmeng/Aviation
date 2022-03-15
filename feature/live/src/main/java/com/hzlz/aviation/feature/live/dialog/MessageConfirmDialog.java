package com.hzlz.aviation.feature.live.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.live.databinding.MessageConfirmDialogBinding;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.hzlz.aviation.feature.live.R;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc : 信息提示
 **/
public class MessageConfirmDialog extends GVideoCenterDialog {

    private MessageConfirmDialogBinding mLayoutBinding;
    private View.OnClickListener confirm,cancel;

    public MessageConfirmDialog(Context context) {
        super(context);

        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.message_confirm_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mLayoutBinding.cancel.setOnClickListener(v -> {
            if (cancel!=null){
                cancel.onClick(v);
            }
            dismiss();
        });

        mLayoutBinding.confirm.setOnClickListener(v -> {
            if (confirm!=null){
                confirm.onClick(v);
            }
            dismiss();
        });
    }

    /**
     * 显示单按钮
     */
    public void setSingleButton(String confirmString, View.OnClickListener confirm){
        this.confirm = confirm;
        mLayoutBinding.cancel.setVisibility(View.GONE);
        mLayoutBinding.confirm.setText(confirmString);
    }

    /**
     *  显示2个按钮
     */
    public void setDoubleButton(String cancelString,View.OnClickListener cancelListener,String confirmString, View.OnClickListener confirmListener){
        this.confirm = confirmListener;
        this.cancel = cancelListener;
        mLayoutBinding.confirm.setText(confirmString);
        mLayoutBinding.cancel.setText(cancelString);
        mLayoutBinding.cancel.setVisibility(View.VISIBLE);
    }


    public void setMessage(String content){
        setMessage("",content);
    }

    public void setMessage(String title,String content){
        if (!TextUtils.isEmpty(title)) {
            mLayoutBinding.title.setText(title);
            mLayoutBinding.title.setVisibility(View.VISIBLE);
        }else {
            mLayoutBinding.title.setVisibility(View.GONE);
        }
        mLayoutBinding.content.setText(content);
    }

}
