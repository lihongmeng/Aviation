package com.jxntv.base.view.floatwindow;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.core.view.ViewCompat;

import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AsyncUtils;

import java.lang.ref.WeakReference;


/**
 * 悬浮窗管理器
 */
public class FloatingView implements IFloatingView {

    private FloatingWindowView mFloatingView;
    private static volatile FloatingView mInstance;
    private WeakReference<FrameLayout> mContainer;
    private ViewGroup.LayoutParams mLayoutParams = getParams();


    private FloatingView() {
    }

    public static FloatingView get() {
        if (mInstance == null) {
            synchronized (FloatingView.class) {
                if (mInstance == null) {
                    mInstance = new FloatingView();
                }
            }
        }
        return mInstance;
    }

    @Override
    public FloatingView remove() {

        AsyncUtils.runOnUIThread(() -> {
            if (mFloatingView == null) {
                return;
            }
            if (ViewCompat.isAttachedToWindow(mFloatingView) && getContainer() != null) {
                getContainer().removeView(mFloatingView);
            }
            mFloatingView = null;
        });

        return this;
    }

    private void ensureFloatingView() {
        synchronized (this) {
            if (mFloatingView != null) {
                return;
            }
            mFloatingView = new FloatingWindowView(GVideoRuntime.getAppContext());
            mFloatingView.setLayoutParams(mLayoutParams);
            //延迟添加view
            addViewToWindow(mFloatingView);
            startLoading();
        }
    }

    @Override
    public FloatingView add() {
        ensureFloatingView();
        return this;
    }

    @Override
    public FloatingView attach(Activity activity) {
        attach(getActivityRoot(activity));
        return this;
    }

    @Override
    public FloatingView attach(FrameLayout container) {
        if (container == null || mFloatingView == null) {
            mContainer = new WeakReference<>(container);
            return this;
        }
        if (mFloatingView.getParent() == container) {
            return this;
        }
        if (mFloatingView.getParent() != null) {
            ((ViewGroup) mFloatingView.getParent()).removeView(mFloatingView);
        }
        mContainer = new WeakReference<>(container);
        container.addView(mFloatingView);
        return this;
    }

    @Override
    public FloatingView detach(Activity activity) {
        detach(getActivityRoot(activity));
        return this;
    }

    @Override
    public FloatingView detach(FrameLayout container) {
        if (mFloatingView != null && container != null && ViewCompat.isAttachedToWindow(mFloatingView)) {
            container.removeView(mFloatingView);
        }
        if (getContainer() == container) {
            mContainer = null;
        }
        return this;
    }

    @Override
    public FloatingMagnetView getView() {
        return mFloatingView;
    }

    @Override
    public FloatingView startLoading() {
        if (mFloatingView != null) {
            mFloatingView.setStartLoading();
        }
        return this;
    }

    @Override
    public FloatingView finishLoading() {
        if (mFloatingView != null) {
            mFloatingView.setFinishLoading();
        }
        return this;
    }

    @Override
    public FloatingView setProgress(int progress) {
        if (mFloatingView != null) {
            mFloatingView.setProgress(progress);
        }
        return this;
    }

    @Override
    public FloatingView setTipText(String loadingText, String finishText) {
        if (mFloatingView != null) {
            mFloatingView.setTipText(loadingText, finishText);
        }
        return this;
    }

    private void addViewToWindow(final View view) {
        if (getContainer() == null) {
            return;
        }
        getContainer().addView(view);
    }

    private FrameLayout getContainer() {
        if (mContainer == null) {
            return null;
        }
        return mContainer.get();
    }

    private FrameLayout.LayoutParams getParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.START;
        return params;
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}