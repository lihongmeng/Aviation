package com.jxntv.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.jxntv.async.config.GlobalExecutorConfig;
import com.jxntv.async.executor.GlobalExecutorEntity;
import com.jxntv.async.executor.TempGlobalExecutorEntity;
import com.jxntv.async.model.GlobalTask;
import com.jxntv.utils.LibConfigUtils;

/**
 * 全局线程池调度器
 */
public class GlobalExecutorScheduler {

    /** TAG **/
    private static final String TAG = "GlobalExecutorScheduler";
    /** DEBUG **/
    private static final boolean DEBUG = LibConfigUtils.GLOBAL_DEBUG;
    /** 全局线程池调度器单例 **/
    private static volatile GlobalExecutorScheduler sInstance = null;

    /** Scheduler调度线程 **/
    private Handler mSchedulerHandler;
    /** 任务队列 **/
    private GlobalTaskQueue mGlobalTaskQueue;

    /** 消息：插入任务 **/
    private static final int SCHEDULER_INSERT_TASK = 1;
    /** 消息：改变策略 **/
    private static final int SCHEDULER_CHECK_POLICY = 2;
    /** 消息：等待新的任务 **/
    private static final int SCHEDULER_WAITING_TASK = 3;

    /** user任务对应的线程池，最高优 **/
    private GlobalExecutorEntity mUserExecutor;
    /** 普通任务对应的线程池 **/
    private GlobalExecutorEntity mNormalExecutor;
    /** 可延迟的后台任务对应的线程池 **/
    private GlobalExecutorEntity mBackgroundExecutor;

    /** 临时扩容线程池 **/
    private TempGlobalExecutorEntity mTempExecutor;


    /**
     * 获取全局线程池调度器实例。
     *
     * @return 全局线程池调度器实例
     */
    public static GlobalExecutorScheduler getInstance() {
        if (sInstance == null) {
            synchronized (GlobalExecutorScheduler.class) {
                if (sInstance == null) {
                    sInstance = new GlobalExecutorScheduler();
                }
            }
        }
        return sInstance;
    }

    /**
     * 调度器构造方法
     */
    private GlobalExecutorScheduler() {
        // 1. 生成对应的任务等待队列
        mGlobalTaskQueue = new GlobalTaskQueue();

        // 2. 生成对应的线程池
        mUserExecutor = new GlobalExecutorEntity(GlobalExecutorConfig.USER_THREAD_CORE_NUM,
                GlobalExecutor.PRIORITY_USER);
        mNormalExecutor = new GlobalExecutorEntity(GlobalExecutorConfig.NORMAL_CORE_NUM,
                GlobalExecutor.PRIORITY_NORMAL);
        mBackgroundExecutor = new GlobalExecutorEntity(GlobalExecutorConfig.BACKGROUND_THREAD_CORE_NUM,
                GlobalExecutor.PRIORITY_BACKGROUND);
        mTempExecutor = new TempGlobalExecutorEntity();

        // 3. 生成Scheduler调度线程对应的handler
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();

        mSchedulerHandler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case SCHEDULER_INSERT_TASK:
                        insertTask(msg.obj);
                        trySchedulerTask();
                        break;
                    case SCHEDULER_CHECK_POLICY:
                        checkPolicy();
                        break;
                    case SCHEDULER_WAITING_TASK:
                        trySchedulerTask();
                        break;
                    default:
                        break;
                }
            }
        };

    }


    /**
     * 处理插入任务
     *
     * @param object  待插入任务实体
     */
    private void insertTask(Object object) {
        if (object instanceof GlobalTask) {
            GlobalTask task = (GlobalTask) object;
            mGlobalTaskQueue.insertTask(task);
        }
    }

    /**
     * 尝试改变策略（是否需要开启/关闭临时线程池）
     */
    private void checkPolicy() {

        // 如果开启临时线程池，开启完成后需要通知调度器立即开始调度任务
        if (checkOpenTemp()) {
            mTempExecutor.openTempExecutor();
            postWaitingTaskMsg();
            return;
        }

        if (checkStopTemp()) {
            mTempExecutor.stopTempExecutor();
        }

    }

    /**
     * 检查是否应开启临时策略
     */
    private boolean checkOpenTemp() {
        if (mTempExecutor.isOpen()) {
            return false;
        }
        if (mGlobalTaskQueue.getTotalWaitWeight() > GlobalExecutorConfig.OPEN_TEMP_WEIGHT_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否应关闭临时策略
     */
    private boolean checkStopTemp() {
        if (!mTempExecutor.isOpen()) {
            return false;
        }
        if (mGlobalTaskQueue.getTotalWaitWeight() < GlobalExecutorConfig.STOP_TEMP_WEIGHT_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     *  尝试调度任务
     */
    private void trySchedulerTask() {
        int handleTaskNum = 0;
        // 循环调度处理任务，直到无法继续置入任务
        while(tryHandleNextTask()) {
            handleTaskNum++;
        }
        if (DEBUG) {
            Log.d(TAG, "handle task num : " + handleTaskNum);
        }
        // 尝试检查策略是否需要升级
        postCheckPolicyMsg();
    }

    /**
     *  处理下一个任务
     */
    private boolean tryHandleNextTask() {
        GlobalTask task = mGlobalTaskQueue.getNextTask();
        if (task == null) {
            return false;
        }

       if (mUserExecutor.executeTask(task)
                || mNormalExecutor.executeTask(task)
                || mBackgroundExecutor.executeTask(task)
                || mTempExecutor.executeTask(task)) {
           mGlobalTaskQueue.removeTask(task);
       }
       return false;
    }

    /**
     *  发送插入任务事件
     */
    public void postTaskDelayMsg(Runnable runnable, String taskName, int priority, long delayTime) {
        Message msg = Message.obtain();
        msg.what = SCHEDULER_INSERT_TASK;
        msg.obj = GlobalTask.build(runnable, taskName, priority);
        if (delayTime == 0) {
            mSchedulerHandler.sendMessage(msg);
            return;
        }
        mSchedulerHandler.sendMessageDelayed(msg, delayTime);
    }

    /**
     *  发送队列等待事件
     */
    public void postWaitingTaskMsg() {
        Message msg = Message.obtain();
        msg.what = SCHEDULER_WAITING_TASK;
        mSchedulerHandler.sendMessage(msg);
    }

    /**
     *  发送检查策略事件
     */
    private void postCheckPolicyMsg() {
        Message msg = Message.obtain();
        msg.what = SCHEDULER_CHECK_POLICY;
        mSchedulerHandler.sendMessage(msg);
    }
}
