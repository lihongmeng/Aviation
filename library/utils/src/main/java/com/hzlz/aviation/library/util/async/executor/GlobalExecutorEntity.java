package com.hzlz.aviation.library.util.async.executor;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.util.LibConfigUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;
import com.hzlz.aviation.library.util.async.GlobalExecutorScheduler;
import com.hzlz.aviation.library.util.async.config.GlobalExecutorConfig;
import com.hzlz.aviation.library.util.async.interf.IGlobalTaskCallback;
import com.hzlz.aviation.library.util.async.model.GlobalTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池实体对象
 */
public class GlobalExecutorEntity {

    /** TAG **/
    private static final String TAG = "GlobalExecutorEntity";
    /** DEBUG **/
    private static final boolean DEBUG = LibConfigUtils.GLOBAL_DEBUG;

    /** 线程池实体 **/
    protected ThreadPoolExecutor mThreadPoolExecutor;
    /** 支持等级 **/
    protected @GlobalExecutor.TaskPriority int mMaxPriority;
    /** 最大并发容量任务数 **/
    protected int mMaxThreadNum;
    /** 线程池当前执行任务数 **/
    protected int mCurrentThreadNum;

    /**
     * 默认无参构造函数
     */
    protected GlobalExecutorEntity() {
    }

    /**
     * 构造函数
     *
     * @param poolSize      线程池核心线程数
     * @param maxPriority   支持优先级
     */
    public GlobalExecutorEntity(int poolSize, @GlobalExecutor.TaskPriority int maxPriority) {
        mThreadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize,
                    GlobalExecutorConfig.POOL_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());
        mMaxPriority = maxPriority;
        mMaxThreadNum = poolSize;
        mCurrentThreadNum = 0;
    }

    /**
     * 执行任务
     *
     * @param task  待执行任务
     */
    public synchronized boolean executeTask(@NonNull final GlobalTask task) {
        // 1. 检查任务是否可用
        if (!isExecutorAvailable(task)) {
            return false;
        }
        // 2 设置对应的回调接口
        task.setTaskCallback(new IGlobalTaskCallback() {
            @Override
            public void onStartHandleTask() {
                setThread(task);
            }

            @Override
            public void onEndHandleTsk() {
                mCurrentThreadNum--;
                GlobalExecutorScheduler.getInstance().postWaitingTaskMsg();
            }

        });

        // 3. 执行任务
        try {
            mThreadPoolExecutor.execute(task);
        } catch (RejectedExecutionException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return false;
        }
        mCurrentThreadNum++;
        return true;
    }

    /**
     * 检查任务是否可执行
     *
     * @param task  待执行任务
     */
    protected synchronized boolean isExecutorAvailable(@NonNull GlobalTask task) {
        // 1. 检查策略等级是否符合标准
        if (task.getPriority() > mMaxPriority) {
            return false;
        }

        // 2. 检查线程是否已全部被占用
        if (mCurrentThreadNum >= mMaxThreadNum) {
            return false;
        }
        return true;
    }

    /**
     * 检查任务是否可执行
     *
     * @param task  待执行任务
     */
    protected void setThread(GlobalTask task) {
        int priority = task.getPriority();
        Thread currentThread = Thread.currentThread();

        // 1. 设置task名
        currentThread.setName(task.getTaskName());

        // 2. 设置thread级别
        switch (priority) {
            case com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_USER:
                currentThread.setPriority(GlobalExecutorConfig.USER_THREAD_PRIORITY);
            case com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_NORMAL:
                currentThread.setPriority(GlobalExecutorConfig.NORMAL_THREAD_PRIORITY);
            case com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_BACKGROUND:
                currentThread.setPriority(GlobalExecutorConfig.BACKGROUND_THREAD_PRIORITY);
            default:
                currentThread.setPriority(GlobalExecutorConfig.BACKGROUND_THREAD_PRIORITY);
        }
    }
}
