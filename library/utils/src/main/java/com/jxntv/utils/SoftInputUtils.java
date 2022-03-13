package com.jxntv.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 软键盘全局辅助类
 */
public class SoftInputUtils {

    /**
     * 显示软键盘
     *
     * @param editText 编辑text
     * @param context  对应上下文环境
     * @param delayed  延迟时间
     */
    public static void showSoftInput(@NonNull EditText editText, @NonNull Context context, long delayed) {
        AsyncUtils.runOnUIThread(() -> {
            editText.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, delayed);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 对应的activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity != null) {
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                hideSoftInput(focusView.getWindowToken(), activity);
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity          对应的activity
     * @param focusedChildView  焦点View
     */
    public static void hideSoftInput(Activity activity, View focusedChildView) {
        if (activity == null || focusedChildView == null) {
            return;
        }
        hideSoftInput(focusedChildView.getWindowToken(), activity);
    }

    /**
     * 隐藏软键盘
     *
     * @param windowToken 对应窗口token
     * @param context     对应上下文环境
     */
    public static void hideSoftInput(@Nullable IBinder windowToken, @NonNull Context context) {
        if (windowToken == null) {
            return;
        }
        InputMethodManager imm =
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

}


