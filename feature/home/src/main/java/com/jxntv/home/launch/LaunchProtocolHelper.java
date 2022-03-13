package com.jxntv.home.launch;

import android.content.Context;
import android.view.View;
import com.jxntv.home.splash.dialog.SplashConfirmDialog;
import com.jxntv.home.splash.dialog.SplashProtocolDialog;

/**
 * 启动协议帮助累
 */
public class LaunchProtocolHelper {

    /**
     * 首次启动任务，显示确认协议
     *
     * @param context   上下文环境
     * @param listener   结果监听
     */
    public static void onLaunchInstall(Context context, OnProtocolResultListener listener) {
        SplashProtocolDialog protocolDialog = new SplashProtocolDialog(context);
        SplashConfirmDialog confirmDialog = new SplashConfirmDialog(context, listener);
        protocolDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                protocolDialog.cancel();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        protocolDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                protocolDialog.cancel();
                confirmDialog.showConfirm(true);
            }
        });

        confirmDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.cancel();
                protocolDialog.show();
            }
        });
        protocolDialog.setCancelable(false);
        protocolDialog.setCanceledOnTouchOutside(false);
        protocolDialog.show();
    }

    /**
     * 协议结果监听器
     */
    public interface OnProtocolResultListener {

        /**
         * 确认统一协议
         */
        void onConfirm();

        /**
         * 取消，拒绝协议
         */
        void onCancel();
    }
}
