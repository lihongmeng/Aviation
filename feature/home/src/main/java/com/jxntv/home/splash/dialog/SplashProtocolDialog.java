package com.jxntv.home.splash.dialog;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.dialog.GVideoCenterDialog;
import com.jxntv.home.R;
import com.jxntv.home.databinding.LayoutSplashProtocolDialogBinding;
import com.jxntv.ioc.PluginManager;

public class SplashProtocolDialog extends GVideoCenterDialog {

    /**
     * 持有的dataBind
     */
    private LayoutSplashProtocolDialogBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public SplashProtocolDialog(@NonNull Context context) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_splash_protocol_dialog, null, false);
        initContent(mLayoutBinding.content);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        setCanceledOnTouchOutside(false);
    }

    /**
     * 设置取消监听器
     *
     * @param listener 取消监听器
     */
    public void setCancelListener(View.OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mLayoutBinding.splashProtocolCancel.setOnClickListener(listener);

    }

    /**
     * 设置确定监听器
     *
     * @param listener 取消监听器
     */
    public void setConfirmListener(View.OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mLayoutBinding.splashProtocolConfirm.setOnClickListener(listener);
    }

    private void initContent(TextView contentView) {
        String userProtocol = contentView.getContext().getString(R.string.user_protocol);
        String privacyProtocol = contentView.getContext().getString(R.string.privacy_protocol);
        int color = Color.RED;
        SpannableString spannableString = new SpannableString(contentView.getText());
        setSpanContent(spannableString, userProtocol, color, new Runnable() {
            @Override
            public void run() {
                String url = PluginManager.get(H5Plugin.class).getUserAgreementUrl();
                PluginManager.get(WebViewPlugin.class).startWebViewActivity(contentView.getContext(),
                        url, userProtocol);
            }
        });
        setSpanContent(spannableString, privacyProtocol, color, new Runnable() {
            @Override
            public void run() {
                String url = PluginManager.get(H5Plugin.class).getPrivacyPolicyUrl();
                PluginManager.get(WebViewPlugin.class).startWebViewActivity(contentView.getContext(),
                        url, privacyProtocol);
            }
        });

        contentView.setText(spannableString);
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setSpanContent(SpannableString spannableString, String text, int color, Runnable clickRunnable) {
        String content = spannableString.toString();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(text)) {
            return;
        }
        if (!content.contains(text)) {
            return;
        }
        int start = content.indexOf(text);
        int end = start + text.length();
        // 设置点击
        spannableString.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (clickRunnable != null) {
                            clickRunnable.run();
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                },
                start,
                end,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
        // 设置颜色
        spannableString.setSpan(
                new ForegroundColorSpan(color),
                start,
                end,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );

    }
}
