package com.jxntv.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

/**
 * 常用异步工具类
 */
public class AsyncUtils {

    /** 持有的主线程handler */
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 抛到ui线程
     *
     * @param runnable      抛到主线程的runnable
     * @param delayed       延迟时间
     */
    public static void runOnUIThread(@NonNull Runnable runnable, long delayed) {
        sHandler.postDelayed(runnable, delayed);
    }

    /**
     * 抛到ui线程
     *
     * @param runnable      抛到主线程的runnable
     */
    public static void runOnUIThread(@NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    /**
     * 是否在主线程
     *
     * @return 是否在主线程
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }
}
