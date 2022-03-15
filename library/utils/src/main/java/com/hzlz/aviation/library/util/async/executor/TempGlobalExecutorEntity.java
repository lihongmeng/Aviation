package com.hzlz.aviation.library.util.async.executor;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.util.async.config.GlobalExecutorConfig;
import com.hzlz.aviation.library.util.async.model.GlobalTask;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 临时线程池实体对象
 */
public class TempGlobalExecutorEntity extends GlobalExecutorEntity {

    /** 是否开启临时线程池 **/
    private boolean mIsOpenTempExecutor;

    /**
     * 构造函数
     */
    public TempGlobalExecutorEntity() {
        mThreadPoolExecutor = new ThreadPoolExecutor(0, GlobalExecutorConfig.TEMP_POOL_MAX_SIZE,
                GlobalExecutorConfig.POOL_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());
        mIsOpenTempExecutor = false;
    }

    @Override
    protected synchronized boolean isExecutorAvailable(@NonNull GlobalTask task) {
        // 临时线程池仅检查是否开启标记
        if (mIsOpenTempExecutor) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前临时线程池是否处于开启状态
     *
     * @return 当前线程池开启状态
     */
    public boolean isOpen() {
        return mIsOpenTempExecutor;
    }

    /**
     * 开启临时线程池
     */
    public void openTempExecutor() {
        if (mIsOpenTempExecutor) {
            return;
        }
        mIsOpenTempExecutor = true;

    }

    /**
     * 关闭临时线程池
     */
    public void stopTempExecutor() {
        if (!mIsOpenTempExecutor) {
            return;
        }
        mIsOpenTempExecutor = false;
    }
}
