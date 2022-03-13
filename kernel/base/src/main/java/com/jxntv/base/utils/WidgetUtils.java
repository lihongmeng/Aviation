package com.jxntv.base.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.jxntv.runtime.GVideoRuntime;
import java.lang.reflect.Field;

public class WidgetUtils {

    private static Integer sStatusBarHeight;

    public static boolean isMIUI() {
        return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi");
    }

    public static boolean isFlyme() {
        try {
            return Build.class.getMethod("hasSmartBar") != null;
        } catch (Exception e) {}
        return false;
    }

    public static int getStatusBarHeight() {
        if (sStatusBarHeight != null) {
            return sStatusBarHeight;
        }
        int resourceId = GVideoRuntime.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            sStatusBarHeight = GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(resourceId);
        } else {
            try {
                @SuppressLint("PrivateApi")
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Field field = c.getField("status_bar_height");
                field.setAccessible(true);
                sStatusBarHeight = GVideoRuntime.getAppContext().getResources()
                        .getDimensionPixelSize(Integer.parseInt(field.get(c.newInstance()).toString()));
            } catch (Throwable throwable) {}
        }
        if (sStatusBarHeight <= 0) {
            sStatusBarHeight = dip2px(25f);
        }
        return sStatusBarHeight;
    }


    public static boolean isFullScreen(@NonNull Window window) {
        return (window.getAttributes().flags
                & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * GVideoRuntime.getAppContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
