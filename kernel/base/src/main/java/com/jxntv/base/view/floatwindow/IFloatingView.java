package com.jxntv.base.view.floatwindow;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;

/**
 *
 *  全局悬浮布局
 */

public interface IFloatingView {

    FloatingView remove();

    FloatingView add();

    /**
     * 布局附着
     */
    FloatingView attach(Activity activity);

    FloatingView attach(FrameLayout container);

    /**
     * 取消附着
     */
    FloatingView detach(Activity activity);

    FloatingView detach(FrameLayout container);

    FloatingMagnetView getView();

    FloatingView startLoading();

    FloatingView finishLoading();

    FloatingView setProgress(int progress);

    FloatingView setTipText(String loadingText, String finishText);

}
