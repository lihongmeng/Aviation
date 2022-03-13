package com.jxntv.live.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.live.R;
import com.jxntv.live.databinding.LiveMessgeDialogBinding;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc : 直播结束统计
 **/
public class LiveEndMessageDialog extends GVideoCenterDialog {

    private LiveMessgeDialogBinding mLayoutBinding;

    public LiveEndMessageDialog(Context context, View.OnClickListener back) {
        super(context);

        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.live_messge_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.y = - getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_100DP);
        getWindow().setAttributes(params);
        mLayoutBinding.back.setOnClickListener(v -> {
            back.onClick(v);
            dismiss();
        });
    }

    @SuppressLint("DefaultLocale")
    public void setData(int lookCount, int praiseCount){
        mLayoutBinding.lookCount.setText(String.format("%d", lookCount));
        mLayoutBinding.praiseCount.setText(String.format("%d", praiseCount));
    }

}
