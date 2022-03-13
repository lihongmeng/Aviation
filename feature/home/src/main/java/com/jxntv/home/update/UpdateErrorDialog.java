package com.jxntv.home.update;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.model.update.UpdateModel;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.home.R;
import com.jxntv.home.databinding.LayoutErrorDialogBinding;
import com.jxntv.home.databinding.LayoutUpdateForceDialogBinding;
import com.jxntv.utils.LogUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

/**
 * 更新错误弹窗
 */
public class UpdateErrorDialog extends GVideoCenterDialog {

    private static final int MAX_PROGRESS = 100;
    /**
     * 持有的dataBind
     */
    private LayoutErrorDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public UpdateErrorDialog(@NonNull Context context, View.OnClickListener clickListener) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_error_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        setCanceledOnTouchOutside(false);
        mLayoutBinding.confirm.setOnClickListener(view -> {
            clickListener.onClick(view);
            dismiss();
        });
    }

}
