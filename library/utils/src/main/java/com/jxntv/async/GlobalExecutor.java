package com.jxntv.async;

import android.text.TextUtils;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import com.jxntv.utils.LibConfigUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 全局线程池调度工具类
 */
public class GlobalExecutor implements Executor {

    /** TAG **/
    private static final String TAG = "GlobalExecutorEntity";
    /** DEBUG **/
    private static final boolean DEBUG = LibConfigUtils.GLOBAL_DEBUG;
    /** task 默认任务名 **/
    private static final String DEFAULT_TASK_NAME = "default";

    /** 不限制线程数量，立即执行的线程池，适用于极高优任务 **/
    private static volatile Executor sImmediateExecutor;
    /** 任务命名统一增加前缀 **/
    private static final String DEFAULT_TASK_NAME_PREFIX = "g_executor_";

    /** 第一优先级：User Related **/
    public static final int PRIORITY_USER = 0;
    /** 第二优先级：需及时执行的非UI相关任务 **/
    public static final int PRIORITY_NORMAL = 1;
    /** 第三优先级：允许delay的后台任务 **/
    public static final int PRIORITY_BACKGROUND = 2;
    /** 默认优先级 **/
    public static final int PRIORITY_DEFAULT = PRIORITY_BACKGROUND;

    /** globalExecutor 实例对应的优先级 **/
    private @TaskPriority int mPriority;

    @IntDef({PRIORITY_USER, PRIORITY_NORMAL, PRIORITY_BACKGROUND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TaskPriority {
    }

    /**
     * 用于外部接入统一线程池，初始化时即设置优先级，后续使用该对象抛入统一线程池的线程，均使用该约定优先级
     *
     * @param priority  约定优先级
     */
    public GlobalExecutor(@TaskPriority int priority) {
        mPriority = priority;
    }

    /**
     * 获取立即执行的线程池（仅适用于高优任务)
     *
     * @param runnable  待处理的任务
     * @param taskName  任务名
     */
    public static boolean postOnImmediate(@NonNull final Runnable runnable,
                                          @NonNull String taskName) {
        if (!checkImmediateLimit(taskName)) {
            return false;
        }
        getImmediateExecutor().execute(runnable);
        return true;
    }

    /**
     * 获取立即执行的线程池（仅适用于高优任务)
     */
    private static Executor getImmediateExecutor() {
        if (sImmediateExecutor == null) {
            synchronized (GlobalExecutor.class) {
                if (sImmediateExecutor == null) {
                    sImmediateExecutor = Executors.newCachedThreadPool();
                }
            }
        }
        return sImmediateExecutor;
    }

    /**
     * 检查是否符合使用紧急任务线程池条件（目前无实现，后续可以接入白名单等）
     *
     * @param taskName  任务名
     */
    private static boolean checkImmediateLimit(String taskName) {
        return true;
    }


    @Override
    public void execute(Runnable runnable) {
        execute(runnable, DEFAULT_TASK_NAME, mPriority, 0);
    }

    /**
     * 置入非延时任务
     *
     * @param runnable  待执行任务
     * @param taskName  任务名
     * @param priority  任务优先级
     */
    public static void execute(@NonNull Runnable runnable,
                                @NonNull String taskName,
                                @GlobalExecutor.TaskPriority int priority) {
        execute(runnable, taskName, priority, 0);
    }

    /**
     * 检查是否符合使用紧急任务线程池条件（目前无实现，后续可以接入白名单等）
     *
     * @param runnable  待处理任务
     * @param taskName  任务名
     * @param priority  任务优先级
     * @param delayTime 等待时间
     */
    public static void execute(@NonNull Runnable runnable,
                                @NonNull String taskName,
                                @GlobalExecutor.TaskPriority int priority,
                                long delayTime) {
        if (runnable == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("GlobalExecutorEntity error! can't handle null task!");
            }
            return;
        }

        if (TextUtils.isEmpty(taskName)) {
            if (DEBUG) {
                throw new IllegalArgumentException("GlobalExecutorEntity error! can't handle with empty task name!");
            }
            taskName = DEFAULT_TASK_NAME;
            taskName = DEFAULT_TASK_NAME_PREFIX + taskName;
        }

        if (priority != PRIORITY_USER && priority != PRIORITY_NORMAL && priority != PRIORITY_BACKGROUND) {
            if (DEBUG) {
                throw new IllegalArgumentException("GlobalExecutorEntity error! can't handle with error priority!");
            }
            priority = PRIORITY_DEFAULT;
        }

        if (delayTime < 0) {
            if (DEBUG) {
                throw new IllegalArgumentException("GlobalExecutorEntity error! can't handle with error delayTime!");
            }
            delayTime = 0;
        }
        GlobalExecutorScheduler.getInstance().postTaskDelayMsg(runnable, taskName, priority, delayTime);
    }

}
