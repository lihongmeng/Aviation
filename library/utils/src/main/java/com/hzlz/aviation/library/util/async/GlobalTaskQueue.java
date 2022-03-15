package com.hzlz.aviation.library.util.async;

import android.os.SystemClock;

import com.hzlz.aviation.library.util.async.config.GlobalExecutorConfig;
import com.hzlz.aviation.library.util.async.model.GlobalTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局线程池任务等待调度队列
 */
public class GlobalTaskQueue {

    /** Ui级别任务task list **/
    private List<GlobalTask> mUITaskList = new ArrayList<>();
    /** in time级别任务task list **/
    private List<GlobalTask> mInTimeTaskList = new ArrayList<>();
    /** background级别任务task list **/
    private List<GlobalTask> mBackgroundTaskList = new ArrayList<>();

    /**
     * 插入任务
     *
     * @param task  待插入队列的任务
     */
    public void insertTask(GlobalTask task) {
        // 1. 判断任务合法性
        if (task == null || !task.isValid()) {
            return;
        }
        // 2. 插入任务，更新优先级
        getListByPriority(task.getPriority()).add(task);
        task.updateInsertTime();
    }

    /**
     * 获取下一个任务
     *
     * @return 下一个待执行的全局任务
     */
    public GlobalTask getNextTask() {
        // 按照优先级顺序获取任务
        if (!mUITaskList.isEmpty()) {
            return mUITaskList.get(0);
        }
        if (!mInTimeTaskList.isEmpty()) {
            return mInTimeTaskList.get(0);
        }
        if (!mBackgroundTaskList.isEmpty()) {
            return mBackgroundTaskList.get(0);
        }
        return null;
    }

    /**
     * 从等待容器中移除指定的任务
     *
     * @param task  待移除的任务
     */
    public void removeTask(GlobalTask task) {
        if (task == null) {
            return;
        }
        getListByPriority(task.getPriority()).remove(task);
    }

    /**
     * 获取总体等待阈值，用于计算临时线程池开启策略
     *
     * @return 获取总等待阈值
     */
    public long getTotalWaitWeight() {
        long uiTotalWeight = (getListWaitTotalTime(GlobalExecutor.PRIORITY_USER)
                * GlobalExecutorConfig.USER_TASK_WEIGHT) / 1000;
        long inTimeTotalWeight = (getListWaitTotalTime(GlobalExecutor.PRIORITY_NORMAL)
                * GlobalExecutorConfig.NORMAL_TASK_WEIGHT) / 1000;
        long backgroundTotalWeight = (getListWaitTotalTime(GlobalExecutor.PRIORITY_BACKGROUND)
                * GlobalExecutorConfig.BACKGROUND_TASK_WEIGHT) / 1000;
        return uiTotalWeight + inTimeTotalWeight + backgroundTotalWeight;

    }

    /**
     * 根据任务等级查找对应list总等待时间
     *
     * @param priority  对应的任务等级
     * @return 总等待时间
     */
    public long getListWaitTotalTime(@GlobalExecutor.TaskPriority int priority) {
        List<GlobalTask> list = getListByPriority(priority);
        long currentTime = SystemClock.currentThreadTimeMillis();
        long totalTime = 0;
        for (GlobalTask task : list) {
            totalTime += currentTime - task.getInsertTime();
        }
        return totalTime;
    }

    /**
     * 根据task 等级获取对应的列表
     *
     * @param priority  对应的任务等级
     * @return 对应列表
     */
    private List<GlobalTask> getListByPriority(@GlobalExecutor.TaskPriority int priority) {
        switch (priority) {
            case GlobalExecutor.PRIORITY_USER:
                return mUITaskList;
            case GlobalExecutor.PRIORITY_NORMAL:
                return mInTimeTaskList;
            case GlobalExecutor.PRIORITY_BACKGROUND:
            default:
                return mBackgroundTaskList;
        }
    }
}
