package com.jxntv.async.model;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.jxntv.async.GlobalExecutor;
import com.jxntv.async.interf.IGlobalTaskCallback;
import com.jxntv.utils.LibConfigUtils;

public class GlobalTask implements Runnable{

    /** DEBUG **/
    private static final boolean DEBUG = LibConfigUtils.GLOBAL_DEBUG;
    /** TAG **/
    private static final String TAG = "GlobalTask";

    private Runnable mRunnable;
    private IGlobalTaskCallback mGlobalTaskCallback;

    private String mName;

    @GlobalExecutor.TaskPriority
    private int mPriority;

    private long mInsertTime;

    public static GlobalTask build(@NonNull Runnable runnable, @NonNull String taskName, int priority) {
        if (runnable == null || TextUtils.isEmpty(taskName)) {
            if (DEBUG) {
                throw new IllegalArgumentException(TAG + "builder : error params");
            }
            return null;
        }
        if (priority != GlobalExecutor.PRIORITY_USER
                && priority != GlobalExecutor.PRIORITY_NORMAL
                && priority != GlobalExecutor.PRIORITY_BACKGROUND) {
            priority = GlobalExecutor.PRIORITY_DEFAULT;
        }
        return new GlobalTask(runnable, taskName, priority);
    }

    private GlobalTask(Runnable runnable, String name,
                      @GlobalExecutor.TaskPriority int priority) {
        this.mRunnable = runnable;
        this.mName = name;
        this.mPriority = priority;
    }

    public Runnable getTaskRunnable() {
        return mRunnable;
    }

    public String getTaskName() {
        return mName;
    }

    public @GlobalExecutor.TaskPriority int getPriority() {
        return mPriority;
    }

    public long getInsertTime() {
        return mInsertTime;
    }

    public boolean isValid() {
        if (mRunnable == null || TextUtils.isEmpty(mName)) {
            return false;
        }
        return true;
    }

    public void updateInsertTime() {
        mInsertTime = System.currentTimeMillis();
    }

    public void setTaskCallback(IGlobalTaskCallback callback) {
        mGlobalTaskCallback = callback;
    }


    @Override
    public void run() {
        if (mGlobalTaskCallback != null) {
            mGlobalTaskCallback.onStartHandleTask();
        }
        mRunnable.run();
        if (mGlobalTaskCallback != null) {
            mGlobalTaskCallback.onEndHandleTsk();
        }
    }
}
