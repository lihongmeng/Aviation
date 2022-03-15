package com.hzlz.aviation.library.util.async.config;

import com.hzlz.aviation.library.util.SystemUtils;

/**
 * 全局线程池设置类
 */
public class GlobalExecutorConfig {

    /* ----------------------  线程池初始化相关配置start ---------------------- */

    /** 用户等级-线程池核心数配置 **/
    public static final int USER_THREAD_CORE_NUM = 1;
    /** 普通等级-线程池核心数配置 **/
    public static final int NORMAL_CORE_NUM = 2
            + Math.max(((2 * SystemUtils.getCoreCount() - 5) / 3), 0);
    /** 后台等级-线程池核心数配置 **/
    public static final int BACKGROUND_THREAD_CORE_NUM = 3
            + Math.max(((2 * SystemUtils.getCoreCount() - 5) / 3), 0) * 2;
    /** 临时线程池配置 **/
    public static final int TEMP_POOL_MAX_SIZE = Integer.MAX_VALUE;

    /** 线程池默认线程存活时间 **/
    public static final int POOL_KEEP_ALIVE_TIME = 1000;

    /* ----------------------   线程池初始化相关配置end  ---------------------- */


    /* ----------------------  线程设置级别相关配置start ---------------------- */

    /** 用户等级 **/
    public static final int USER_THREAD_PRIORITY = 8;
    /** 普通等级 **/
    public static final int NORMAL_THREAD_PRIORITY = 5;
    /** 后台等级 **/
    public static final int BACKGROUND_THREAD_PRIORITY = 2;

    /* ----------------------   线程设置级别相关配置end  ---------------------- */


    /* ----------------------  线程策略阈值相关配置start ---------------------- */

    /** 用户等级级别权重 **/
    public static final int USER_TASK_WEIGHT = 8;
    /** 普通等级级别权重 **/
    public static final int NORMAL_TASK_WEIGHT = 5;
    /** 后台等级级别权重 **/
    public static final int BACKGROUND_TASK_WEIGHT = 2;

    /** 开启临时线程池阈值 **/
    public static final int OPEN_TEMP_WEIGHT_LIMIT = 20;
    /** 关闭临时线程池阈值 **/
    public static final int STOP_TEMP_WEIGHT_LIMIT = 5;

    /* -----------------------  线程策略阈值相关配置end  ---------------------- */

}
