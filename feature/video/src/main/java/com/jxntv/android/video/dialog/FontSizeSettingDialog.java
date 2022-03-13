package com.jxntv.android.video.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;

import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.DialogFontSizeSettingBinding;
import com.jxntv.base.sp.SharedPrefsWrapper;
import com.jxntv.dialog.GVideoBottomSheetDialog;

/**
 * 录制弹窗
 */
public class FontSizeSettingDialog extends GVideoBottomSheetDialog {

    /**
     * 持有的dataBind
     */
    private final DialogFontSizeSettingBinding mLayoutBinding;

    /**
     * 构造方法
     */
    public FontSizeSettingDialog(Context context, WebView view) {
        super(context);
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog_font_size_setting, null, false);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper("FontSizeSettingDialog");
        int f = prefsWrapper.getInt("font_size",-1);
        if (f>0) {
            view.getSettings().setTextZoom(((f * 100 - 35 * 100) / 35) + 100);
            mLayoutBinding.seekBar.setProgress(f);
        }

        mLayoutBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                view.getSettings().setTextZoom(((i * 100 - 35 * 100) / 35) + 100);
                prefsWrapper.putInt("font_size",i==0?1:i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mLayoutBinding.cancel.setOnClickListener(view1 -> dismiss());

    }

}
