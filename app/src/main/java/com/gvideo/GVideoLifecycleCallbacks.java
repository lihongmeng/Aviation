package com.gvideo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.PushManager;
import com.jxntv.base.view.floatwindow.FloatingView;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 生命周期入口
 */
public class GVideoLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private int mForegroundCount = 0;
    private int mConfigCount = 0;
    private boolean mIsBackground = true;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
//        LogUtils.e(activity.getComponentName().getClassName());
        PushManager.getInstance().removeShortcutBadger();
        FloatingView.get().attach(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (mIsBackground) {
            mIsBackground = false;
            onStatusChanged(true);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                mIsBackground = true;
                onStatusChanged(false);
            }
        }
        FloatingView.get().detach(activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        AppManager.getAppManager().removeActivity(activity);
    }

    private long currentTime = 0;
    private Disposable disposable;
    private boolean isForeground = true;

    private void onStatusChanged(boolean isForeground) {
        GVideoEventBus.get(GVideoRuntime.APP_STATUS_CHANGED, Boolean.class).post(isForeground);
        if (!isForeground) {
            currentTime = System.currentTimeMillis();
        }
        this.isForeground = isForeground;
//        setDisposable();
    }

    public void setDisposable() {
        if (disposable != null) {
            return;
        }
        disposable = Observable.interval(0, 15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //后台10分钟重启app
                    if (System.currentTimeMillis() - currentTime >= 1000 *60 * 10 && !isForeground ) {
                        AppManager.getAppManager().finishAllActivity();
                    }
                });
    }

}
