package com.jxntv.search.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.search.R;
import com.jxntv.search.databinding.LayoutSearchDialogBinding;

/**
 * 搜索删除确定弹窗
 */
public class SearchRemoveHistoryDialog extends GVideoCenterDialog {

    /** 持有的dataBind */
    private LayoutSearchDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public SearchRemoveHistoryDialog(@NonNull Context context) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_search_dialog, null,false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    /**
     * 设置取消监听器
     *
     * @param listener  取消监听器
     */
    public void setCancelListener(View.OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mLayoutBinding.searchDialogCancel.setOnClickListener(listener);

    }

    /**
     * 设置确定监听器
     *
     * @param listener  取消监听器
     */
    public void setConfirmListener(View.OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mLayoutBinding.searchDialogConfirm.setOnClickListener(listener);

    }

}
